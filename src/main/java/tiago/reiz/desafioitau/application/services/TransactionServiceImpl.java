package tiago.reiz.desafioitau.application.services;

import org.springframework.stereotype.Service;
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

    private static final Duration STATISTICS_WINDOW = Duration.ofSeconds(60);

    private final TransactionRepository repository;
    private final Clock clock;

    public TransactionServiceImpl(TransactionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public void register(Transaction transaction) {
        repository.save(transaction);
    }

    /**
     * Calcula as estatisticas das transacoes ocorridas nos ultimos 60 segundos,
     * ou seja, com {@code dataHora} no intervalo {@code [agora - 60s, agora]}.
     */
    @Override
    public Statistics getStatistics() {
        Instant now = clock.instant();
        Instant cutoff = now.minus(STATISTICS_WINDOW);

        repository.deleteOlderThan(cutoff);

        DoubleSummaryStatistics summary = repository.findAll().stream()
                .filter(transaction -> isWithin(transaction, cutoff, now))
                .mapToDouble(Transaction::valor)
                .summaryStatistics();

        return Statistics.from(summary);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private static boolean isWithin(Transaction transaction, Instant cutoff, Instant now) {
        Instant instant = transaction.dataHora().toInstant();
        return !instant.isBefore(cutoff) && !instant.isAfter(now);
    }
}
