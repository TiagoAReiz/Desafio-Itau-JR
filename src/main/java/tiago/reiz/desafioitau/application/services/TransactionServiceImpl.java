package tiago.reiz.desafioitau.application.services;

import org.springframework.stereotype.Service;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.StatisticResponse;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.TransactionRequest;
import tiago.reiz.desafioitau.application.mappers.TransactionMapper;
import tiago.reiz.desafioitau.core.entities.Transaction;
import tiago.reiz.desafioitau.core.interfaces.TransactionRepository;
import tiago.reiz.desafioitau.core.interfaces.TransactionService;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper mapper;
    private final TransactionRepository repo;
    TransactionServiceImpl(TransactionMapper mapper, TransactionRepository repo){
        this.mapper = mapper;
        this.repo = repo;
    }
    @Override
    public void createTransaction(TransactionRequest request) {
        Transaction transaction = mapper.toEntity(request);
        repo.createTransaction(transaction);
    }

    @Override
    public StatisticResponse getStatistics() {
        List<Transaction>transactions = repo.getAllTransactions();
        int count = transactions.size();
        double sum = transactions.stream()
                .mapToDouble(elemento -> elemento.valor())
                .sum();
        double avg = transactions.stream().mapToDouble(elemento -> elemento.valor()).average().orElse(0);
        double min = transactions.stream().mapToDouble(elemento -> elemento.valor()).min().orElse(0);
        double max = transactions.stream().mapToDouble(elemento -> elemento.valor()).max().orElse(0);
        StatisticResponse statistics = new StatisticResponse(count,sum,avg,min,max);
        return statistics;
    }

    @Override
    public void deleteAll(){
        repo.deleteAllTransactions();
    }
}
