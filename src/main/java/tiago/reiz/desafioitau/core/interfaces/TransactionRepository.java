package tiago.reiz.desafioitau.core.interfaces;

import tiago.reiz.desafioitau.core.entities.Transaction;

import java.util.List;

public interface TransactionRepository {
    void createTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    void deleteAllTransactions();
}
