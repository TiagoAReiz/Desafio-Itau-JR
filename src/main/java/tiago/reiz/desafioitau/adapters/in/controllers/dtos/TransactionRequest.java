package tiago.reiz.desafioitau.adapters.in.controllers.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TransactionRequest(
        @NotNull(message = "O valor não pode ser nulo")
        @Positive(message = "O valor enviado deve ser obrigatoriamente maior que zero")
        double valor,

        @PastOrPresent(message = "A data e hora não podem estar no futuro")
        LocalDateTime dataHora) {
}
