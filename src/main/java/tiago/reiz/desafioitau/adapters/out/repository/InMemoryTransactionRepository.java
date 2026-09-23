package tiago.reiz.desafioitau.adapters.out.repository;

import org.springframework.stereotype.Repository;
import tiago.reiz.desafioitau.core.entities.Transaction;
import tiago.reiz.desafioitau.core.interfaces.TransactionRepository;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Armazenamento em memoria, sem banco de dados nem cache externo.
 *
 * <p>{@link ConcurrentLinkedQueue} e thread-safe e nao bloqueante: requisicoes simultaneas
 * de {@code POST}, {@code GET} e {@code DELETE} podem ocorrer sem perda de dados nem
 * {@code ConcurrentModificationException} (problema do {@code ArrayList} usado antes).
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }

    @Override
    public void deleteOlderThan(Instant cutoff) {
        transactions.removeIf(transaction -> transaction.dataHora().toInstant().isBefore(cutoff));
    }

    @Override
    public void deleteAll() {
        transactions.clear();
    }
}
