package tiago.reiz.DesafioItau.application.services;

import org.springframework.stereotype.Service;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.statistic;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;
import tiago.reiz.DesafioItau.application.mappers.transactionMap;
import tiago.reiz.DesafioItau.core.entities.transaction;
import tiago.reiz.DesafioItau.core.interfaces.transactionRepository;
import tiago.reiz.DesafioItau.core.interfaces.transactionService;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class transactionServiceImpl implements transactionService {
    private final transactionMap mapper;
    private final transactionRepository repo;
    transactionServiceImpl(transactionMap mapper, transactionRepository repo){
        this.mapper = mapper;
        this.repo = repo;
    }
    @Override
    public void createTransaction(transactionRequest request) {
        transaction transaction = mapper.toEntity(request);
        repo.createTransaction(transaction);
    }

    @Override
    public statistic getStatistics() {
        List<transaction>transactions = repo.getAllTransactions();
        int count = transactions.size();
        double sum = transactions.stream()
                .mapToDouble(elemento -> elemento.getValue())
                .sum();
        double avg = transactions.stream().mapToDouble(elemento -> elemento.getValue()).average().orElse(0);
        double min = transactions.stream().mapToDouble(elemento -> elemento.getValue()).min().orElse(0);
        double max = transactions.stream().mapToDouble(elemento -> elemento.getValue()).max().orElse(0);
        statistic statistics = new statistic(count,sum,avg,min,max);
        return statistics;
    }
}
