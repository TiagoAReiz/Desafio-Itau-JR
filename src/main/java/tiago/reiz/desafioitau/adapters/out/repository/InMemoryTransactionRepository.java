package tiago.reiz.desafioitau.adapters.out.repository;

import org.springframework.stereotype.Repository;
import tiago.reiz.desafioitau.core.entities.Transaction;
import tiago.reiz.desafioitau.core.interfaces.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    List<Transaction> transactions = new ArrayList<>();
    @Override
    public void createTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    @Override
    public void deleteAllTransactions(){
        transactions.clear();
    }
}
