package tiago.reiz.desafioitau.adapters.out.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tiago.reiz.desafioitau.core.entities.Transaction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryTransactionRepositoryTest {

    private static final OffsetDateTime BASE = OffsetDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void salvaERetornaTransacoes() {
        Transaction first = new Transaction(10.0, BASE);
        Transaction second = new Transaction(20.0, BASE.plusSeconds(1));

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll()).containsExactly(first, second);
    }

    @Test
    void findAllRetornaSnapshotImutavel() {
        repository.save(new Transaction(10.0, BASE));

        List<Transaction> snapshot = repository.findAll();
        repository.save(new Transaction(20.0, BASE));

        assertThat(snapshot).hasSize(1);
        assertThatThrownBy(() -> snapshot.add(new Transaction(1.0, BASE)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deleteAllApagaTudo() {
        repository.save(new Transaction(10.0, BASE));
        repository.save(new Transaction(20.0, BASE));

        repository.deleteAll();

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void deleteOlderThanRemoveApenasTransacoesAnterioresAoCorte() {
        Instant cutoff = BASE.toInstant();
        Transaction before = new Transaction(1.0, BASE.minusNanos(1));
        Transaction exactlyAtCutoff = new Transaction(2.0, BASE);
        Transaction after = new Transaction(3.0, BASE.plusSeconds(1));
        repository.save(before);
        repository.save(exactlyAtCutoff);
        repository.save(after);

        repository.deleteOlderThan(cutoff);

        assertThat(repository.findAll()).containsExactly(exactlyAtCutoff, after);
    }

    @Test
    void suportaEscritasConcorrentesSemPerderDados() throws InterruptedException {
        int threads = 16;
        int perThread = 1_000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                start.await();
                for (int i = 0; i < perThread; i++) {
                    repository.save(new Transaction(i, BASE));
                    repository.findAll();
                }
                return null;
            });
        }
        start.countDown();
        executor.shutdown();

        assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        assertThat(repository.findAll()).hasSize(threads * perThread);
    }
}
