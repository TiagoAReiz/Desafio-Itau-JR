package tiago.reiz.DesafioItau.core.interfaces;

import tiago.reiz.DesafioItau.core.entities.transaction;

import java.util.List;

public interface transactionRepository {
    void createTransaction(transaction transaction);

    List<transaction> getAllTransactions();

    void deleteAllTransactions();
}
