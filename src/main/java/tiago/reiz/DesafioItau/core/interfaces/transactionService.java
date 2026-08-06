package tiago.reiz.DesafioItau.core.interfaces;

import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;

public interface transactionService {
    void createTransaction(transactionRequest transaction);
}
