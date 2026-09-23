package tiago.reiz.desafioitau.core.interfaces;

import tiago.reiz.desafioitau.adapters.in.controllers.dtos.StatisticResponse;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.TransactionRequest;

import java.util.List;

public interface TransactionService {
    void createTransaction(TransactionRequest transaction);
    StatisticResponse getStatistics();
    void deleteAll();
}
