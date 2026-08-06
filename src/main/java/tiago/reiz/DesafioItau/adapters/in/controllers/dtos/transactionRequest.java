package tiago.reiz.DesafioItau.adapters.in.controllers.dtos;


import java.time.LocalDateTime;

public record transactionRequest(
        double valor,
        LocalDateTime dataHora) {
}
