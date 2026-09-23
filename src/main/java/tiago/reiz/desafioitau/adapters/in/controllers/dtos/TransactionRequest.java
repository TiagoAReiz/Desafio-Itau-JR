package tiago.reiz.desafioitau.adapters.in.controllers.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

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
        Double valor,

        @NotNull(message = "o campo 'dataHora' e obrigatorio")
        @PastOrPresent(message = "o campo 'dataHora' nao pode estar no futuro")
        OffsetDateTime dataHora) {
}
