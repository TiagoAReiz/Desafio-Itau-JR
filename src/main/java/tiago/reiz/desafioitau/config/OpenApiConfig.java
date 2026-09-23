package tiago.reiz.desafioitau.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadados da documentacao OpenAPI, servida em {@code /swagger-ui.html} e {@code /v3/api-docs}. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Itau - Transacoes e Estatisticas")
                        .version("1.0.0")
                        .description("API REST que recebe transacoes e retorna estatisticas "
                                + "das transacoes ocorridas na janela de tempo configurada (padrao: 60 segundos)."))
                .externalDocs(new ExternalDocumentation()
                        .description("Enunciado oficial do desafio")
                        .url("https://github.com/rafaellins-itau/desafio-itau-vaga-99-junior"));
    }
}
