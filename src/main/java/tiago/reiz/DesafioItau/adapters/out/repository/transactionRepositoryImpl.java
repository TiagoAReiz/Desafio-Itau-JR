package tiago.reiz.DesafioItau.adapters.out.repository;

import org.springframework.stereotype.Repository;
import tiago.reiz.DesafioItau.core.entities.transaction;
import tiago.reiz.DesafioItau.core.interfaces.transactionRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class transactionRepositoryImpl implements transactionRepository {
    List<transaction> transactions = new ArrayList<>();
    @Override
    public void createTransaction(transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<transaction> getAllTransactions() {
        return transactions;
    }
}
