package tiago.reiz.desafioitau.core.interfaces;

import tiago.reiz.desafioitau.core.entities.Statistics;
import tiago.reiz.desafioitau.core.entities.Transaction;

/**
 * Porta de entrada com os casos de uso da API.
 * Trabalha apenas com objetos de dominio, sem depender de DTOs HTTP.
 */
public interface TransactionService {

    void register(Transaction transaction);

    Statistics getStatistics();

    void deleteAll();
}
