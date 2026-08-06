package tiago.reiz.DesafioItau.application.services;

import org.springframework.stereotype.Service;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;
import tiago.reiz.DesafioItau.application.mappers.transactionMap;
import tiago.reiz.DesafioItau.core.entities.transaction;
import tiago.reiz.DesafioItau.core.interfaces.transactionRepository;
import tiago.reiz.DesafioItau.core.interfaces.transactionService;

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
}
