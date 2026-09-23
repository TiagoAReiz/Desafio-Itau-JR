package tiago.reiz.desafioitau.application.mappers;

import org.springframework.stereotype.Component;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.TransactionRequest;
import tiago.reiz.desafioitau.core.entities.Transaction;

@Component
public class TransactionMapper {
    public Transaction toEntity(TransactionRequest dto) {
        return new Transaction(dto.valor(), dto.dataHora());
    }
}
