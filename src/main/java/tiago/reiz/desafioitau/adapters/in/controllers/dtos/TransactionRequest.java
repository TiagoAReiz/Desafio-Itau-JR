package tiago.reiz.desafioitau.adapters.in.controllers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import tiago.reiz.desafioitau.core.entities.Transaction;

import java.time.OffsetDateTime;

/**
 * Corpo da requisicao de {@code POST /transacao}.
 *
 * <p>Os nomes dos campos seguem exatamente o enunciado ({@code valor} e {@code dataHora}).
 * Tipos wrapper sao usados para que a ausencia do campo seja detectada como {@code null}
 * (com {@code double} primitivo, um {@code valor} ausente viraria {@code 0} silenciosamente).
 */
public record TransactionRequest(
        @NotNull(message = "o campo 'valor' e obrigatorio")
        @PositiveOrZero(message = "o campo 'valor' nao pode ser negativo")
        @Schema(description = "Valor da transacao (decimal, >= 0)", example = "123.45", requiredMode = Schema.RequiredMode.REQUIRED)
        Double valor,

        @NotNull(message = "o campo 'dataHora' e obrigatorio")
        @PastOrPresent(message = "o campo 'dataHora' nao pode estar no futuro")
        @Schema(description = "Data/hora ISO 8601 em que a transacao ocorreu (nao pode estar no futuro)",
                example = "2020-08-07T12:34:56.789-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
        OffsetDateTime dataHora) {

    public Transaction toEntity() {
        return new Transaction(valor, dataHora);
    }
}
