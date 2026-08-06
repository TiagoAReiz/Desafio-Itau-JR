package tiago.reiz.DesafioItau.core.interfaces;

import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.statistic;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;

import java.util.List;

public interface transactionService {
    void createTransaction(transactionRequest transaction);
    statistic getStatistics();
}
