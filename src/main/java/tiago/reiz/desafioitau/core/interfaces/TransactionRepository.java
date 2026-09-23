package tiago.reiz.desafioitau.core.interfaces;

import tiago.reiz.desafioitau.core.entities.Transaction;

import java.time.Instant;
import java.util.List;

/**
 * Porta de saida para armazenamento de transacoes.
 * A unica implementacao guarda os dados em memoria, como exige o enunciado.
 */
public interface TransactionRepository {

    void save(Transaction transaction);

    /** Retorna uma copia (snapshot) das transacoes armazenadas. */
    List<Transaction> findAll();

    /** Descarta transacoes anteriores ao instante informado, que nunca mais entrarao na janela. */
    void deleteOlderThan(Instant cutoff);

    void deleteAll();
}
