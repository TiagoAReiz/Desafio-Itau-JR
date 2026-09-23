package tiago.reiz.desafioitau.application.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tiago.reiz.desafioitau.config.StatisticsProperties;
import tiago.reiz.desafioitau.core.entities.Statistics;
import tiago.reiz.desafioitau.core.entities.Transaction;
import tiago.reiz.desafioitau.core.interfaces.TransactionRepository;
import tiago.reiz.desafioitau.core.interfaces.TransactionService;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    static final String STATISTICS_TIMER = "estatistica.calculo";

    private final TransactionRepository repository;
    private final Clock clock;
    private final Duration window;
    private final Timer statisticsTimer;

    public TransactionServiceImpl(TransactionRepository repository,
                                  Clock clock,
                                  StatisticsProperties properties,
                                  MeterRegistry meterRegistry) {
        this.repository = repository;
        this.clock = clock;
        this.window = properties.window();
        this.statisticsTimer = Timer.builder(STATISTICS_TIMER)
                .description("Tempo gasto para calcular as estatisticas de GET /estatistica")
                .register(meterRegistry);
        log.info("Janela de estatisticas configurada para {} segundos", window.toSeconds());
    }

    @Override
    public void register(Transaction transaction) {
        repository.save(transaction);
        log.debug("Transacao registrada: valor={} dataHora={}", transaction.valor(), transaction.dataHora());
    }

    /**
     * Calcula as estatisticas das transacoes ocorridas dentro da janela configurada,
     * ou seja, com {@code dataHora} no intervalo {@code [agora - janela, agora]}.
     *
     * <p>O tempo de calculo e registrado em log e no timer {@value #STATISTICS_TIMER}
     * (exposto em {@code /actuator/metrics/estatistica.calculo}).
     */
    @Override
    public Statistics getStatistics() {
        long start = System.nanoTime();

        Instant now = clock.instant();
        Instant cutoff = now.minus(window);

        repository.deleteOlderThan(cutoff);

        DoubleSummaryStatistics summary = repository.findAll().stream()
                .filter(transaction -> isWithin(transaction, cutoff, now))
                .mapToDouble(Transaction::valor)
                .summaryStatistics();
        Statistics statistics = Statistics.from(summary);

        long elapsedNanos = System.nanoTime() - start;
        statisticsTimer.record(Duration.ofNanos(elapsedNanos));
        log.info("Estatisticas calculadas em {} ms (count={}, janela={}s)",
                String.format("%.3f", elapsedNanos / 1_000_000.0), statistics.count(), window.toSeconds());

        return statistics;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
        log.info("Todas as transacoes foram apagadas");
    }

    private static boolean isWithin(Transaction transaction, Instant cutoff, Instant now) {
        Instant instant = transaction.dataHora().toInstant();
        return !instant.isBefore(cutoff) && !instant.isAfter(now);
    }
}
