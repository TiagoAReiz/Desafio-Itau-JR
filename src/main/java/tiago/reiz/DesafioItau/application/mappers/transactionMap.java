package tiago.reiz.DesafioItau.application.mappers;

import org.springframework.stereotype.Component;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;
import tiago.reiz.DesafioItau.core.entities.transaction;

@Component
public class transactionMap {
    public transaction toEntity(transactionRequest dto) {
        return new transaction(dto.valor(), dto.dataHora());
    }
}
