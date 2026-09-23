package tiago.reiz.desafioitau.application.services;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tiago.reiz.desafioitau.adapters.out.repository.InMemoryTransactionRepository;
import tiago.reiz.desafioitau.config.StatisticsProperties;
import tiago.reiz.desafioitau.core.entities.Statistics;
import tiago.reiz.desafioitau.core.entities.Transaction;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TransactionServiceImplTest {

    private static final Instant NOW = Instant.parse("2024-01-01T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private InMemoryTransactionRepository repository;
    private SimpleMeterRegistry meterRegistry;
    private TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        meterRegistry = new SimpleMeterRegistry();
        service = newService(60);
    }

    private TransactionServiceImpl newService(long windowSeconds) {
        return new TransactionServiceImpl(repository, FIXED_CLOCK,
                new StatisticsProperties(windowSeconds), meterRegistry);
    }

    private static OffsetDateTime secondsAgo(long seconds) {
        return OffsetDateTime.ofInstant(NOW.minusSeconds(seconds), ZoneOffset.UTC);
    }

    @Test
    void semTransacoesRetornaTudoZero() {
        assertThat(service.getStatistics()).isEqualTo(Statistics.EMPTY);
    }

    @Test
    void calculaEstatisticasDasTransacoesNaJanela() {
        service.register(new Transaction(10.0, secondsAgo(1)));
        service.register(new Transaction(20.0, secondsAgo(30)));
        service.register(new Transaction(30.5, secondsAgo(59)));

        Statistics statistics = service.getStatistics();

        assertThat(statistics.count()).isEqualTo(3);
        assertThat(statistics.sum()).isEqualTo(60.5);
        assertThat(statistics.avg()).isCloseTo(20.1666, within(0.0001));
        assertThat(statistics.min()).isEqualTo(10.0);
        assertThat(statistics.max()).isEqualTo(30.5);
    }

    @Test
    void ignoraTransacoesComMaisDe60Segundos() {
        service.register(new Transaction(10.0, secondsAgo(10)));
        service.register(new Transaction(999.0, secondsAgo(61)));
        service.register(new Transaction(500.0, secondsAgo(3600)));

        Statistics statistics = service.getStatistics();

        assertThat(statistics).isEqualTo(new Statistics(1, 10.0, 10.0, 10.0, 10.0));
    }

    @Test
    void consideraOsLimitesDaJanela() {
        service.register(new Transaction(1.0, secondsAgo(0)));   // exatamente agora
        service.register(new Transaction(2.0, secondsAgo(60)));  // exatamente 60s atras
        service.register(new Transaction(4.0, OffsetDateTime.ofInstant(
                NOW.minusSeconds(60).minusMillis(1), ZoneOffset.UTC))); // 60,001s atras

        Statistics statistics = service.getStatistics();

        assertThat(statistics.count()).isEqualTo(2);
        assertThat(statistics.sum()).isEqualTo(3.0);
    }

    @Test
    void comparaInstantesIndependentementeDoFusoHorario() {
        // 08:59:30-03:00 == 11:59:30Z, ou seja, 30 segundos antes de NOW
        service.register(new Transaction(50.0, OffsetDateTime.parse("2024-01-01T08:59:30-03:00")));
        // 08:58:00-03:00 == 11:58:00Z, 2 minutos antes de NOW
        service.register(new Transaction(70.0, OffsetDateTime.parse("2024-01-01T08:58:00-03:00")));

        assertThat(service.getStatistics()).isEqualTo(new Statistics(1, 50.0, 50.0, 50.0, 50.0));
    }

    @Test
    void aceitaTransacaoDeValorZeroNasEstatisticas() {
        service.register(new Transaction(0.0, secondsAgo(5)));
        service.register(new Transaction(10.0, secondsAgo(5)));

        Statistics statistics = service.getStatistics();

        assertThat(statistics.count()).isEqualTo(2);
        assertThat(statistics.min()).isZero();
        assertThat(statistics.avg()).isEqualTo(5.0);
    }

    @Test
    void janelaDeTempoEConfiguravel() {
        TransactionServiceImpl service120 = newService(120);
        service120.register(new Transaction(10.0, secondsAgo(90)));
        service120.register(new Transaction(20.0, secondsAgo(121)));

        assertThat(service120.getStatistics()).isEqualTo(new Statistics(1, 10.0, 10.0, 10.0, 10.0));
    }

    @Test
    void descartaTransacoesForaDaJanelaDoArmazenamento() {
        service.register(new Transaction(10.0, secondsAgo(10)));
        service.register(new Transaction(20.0, secondsAgo(120)));

        service.getStatistics();

        assertThat(repository.findAll()).extracting(Transaction::valor).containsExactly(10.0);
    }

    @Test
    void deleteAllApagaTodasAsTransacoes() {
        service.register(new Transaction(10.0, secondsAgo(1)));
        service.register(new Transaction(20.0, secondsAgo(2)));

        service.deleteAll();

        assertThat(repository.findAll()).isEmpty();
        assertThat(service.getStatistics()).isEqualTo(Statistics.EMPTY);
    }

    @Test
    void registraTempoDeCalculoNaMetrica() {
        service.getStatistics();
        service.getStatistics();

        Timer timer = meterRegistry.find(TransactionServiceImpl.STATISTICS_TIMER).timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(2);
    }
}
