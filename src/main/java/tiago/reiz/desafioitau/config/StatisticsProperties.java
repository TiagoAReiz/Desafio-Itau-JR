package tiago.reiz.desafioitau.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuracao da janela de tempo usada por {@code GET /estatistica}.
 *
 * <p>Padrao: 60 segundos, como no enunciado. Pode ser alterada com a propriedade
 * {@code estatistica.janela-segundos} ou a variavel de ambiente {@code ESTATISTICA_JANELA_SEGUNDOS}.
 */
@Validated
@ConfigurationProperties(prefix = "estatistica")
public record StatisticsProperties(
        @DefaultValue("60")
        @Positive(message = "estatistica.janela-segundos deve ser maior que zero")
        long janelaSegundos) {

    public Duration window() {
        return Duration.ofSeconds(janelaSegundos);
    }
}
