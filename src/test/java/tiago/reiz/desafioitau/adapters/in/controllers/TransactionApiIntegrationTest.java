package tiago.reiz.desafioitau.adapters.in.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao (contexto Spring completo + MockMvc) cobrindo as regras do enunciado.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void limparTransacoes() throws Exception {
        mockMvc.perform(delete("/transacao")).andExpect(status().isOk());
    }

    private static String json(Object valor, Object dataHora) {
        return "{\"valor\": %s, \"dataHora\": %s}".formatted(valor, dataHora);
    }

    private static String quoted(OffsetDateTime dateTime) {
        return "\"" + dateTime + "\"";
    }

    private static OffsetDateTime secondsAgo(long seconds) {
        return OffsetDateTime.now(ZoneOffset.ofHours(-3)).minusSeconds(seconds);
    }

    private ResultActions postTransacao(String body) throws Exception {
        return mockMvc.perform(post("/transacao").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    // ---------- POST /transacao: 201 Created ----------

    @Test
    void postTransacaoValidaRetorna201SemCorpo() throws Exception {
        postTransacao(json(123.45, quoted(secondsAgo(1))))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    void postTransacaoComValorZeroEAceita() throws Exception {
        postTransacao(json(0, quoted(secondsAgo(1))))
                .andExpect(status().isCreated());
    }

    @Test
    void postTransacaoNoPassadoDistanteEAceita() throws Exception {
        // Exemplo exato do enunciado
        postTransacao("{\"valor\": 123.45, \"dataHora\": \"2020-08-07T12:34:56.789-03:00\"}")
                .andExpect(status().isCreated());
    }

    // ---------- POST /transacao: 422 Unprocessable Entity ----------

    @Test
    void postTransacaoComValorNegativoRetorna422SemCorpo() throws Exception {
        postTransacao(json(-0.01, quoted(secondsAgo(1))))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(""));
    }

    @Test
    void postTransacaoNoFuturoRetorna422SemCorpo() throws Exception {
        postTransacao(json(10, quoted(OffsetDateTime.now().plusMinutes(5))))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"dataHora\": \"2020-08-07T12:34:56.789-03:00\"}",
            "{\"valor\": 10}",
            "{\"valor\": null, \"dataHora\": \"2020-08-07T12:34:56.789-03:00\"}",
            "{\"valor\": 10, \"dataHora\": null}"
    })
    void postTransacaoSemCamposObrigatoriosRetorna422SemCorpo(String body) throws Exception {
        postTransacao(body)
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().string(""));
    }

    @Test
    void transacaoRecusadaNaoEArmazenada() throws Exception {
        postTransacao(json(-10, quoted(secondsAgo(1)))).andExpect(status().isUnprocessableContent());

        mockMvc.perform(get("/estatistica"))
                .andExpect(jsonPath("$.count").value(0));
    }

    // ---------- POST /transacao: 400 Bad Request ----------

    @ParameterizedTest
    @ValueSource(strings = {
            "{valor: 10",                                           // JSON malformado
            "",                                                     // corpo vazio
            "[]",                                                   // tipo errado
            "{\"valor\": \"abc\", \"dataHora\": \"2020-08-07T12:34:56.789-03:00\"}", // valor nao numerico
            "{\"valor\": 10, \"dataHora\": \"ontem\"}",             // data fora do ISO 8601
            "{\"valor\": 10, \"dataHora\": \"2020-13-45T99:99:99Z\"}" // data invalida
    })
    void postTransacaoIncompreensivelRetorna400SemCorpo(String body) throws Exception {
        postTransacao(body)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    void postTransacaoSemJsonRetorna415SemCorpo() throws Exception {
        mockMvc.perform(post("/transacao").contentType(MediaType.TEXT_PLAIN).content("valor=10"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(""));
    }

    // ---------- DELETE /transacao ----------

    @Test
    void deleteTransacaoRetorna200SemCorpoEApagaTudo() throws Exception {
        postTransacao(json(10, quoted(secondsAgo(1)))).andExpect(status().isCreated());
        postTransacao(json(20, quoted(secondsAgo(2)))).andExpect(status().isCreated());

        mockMvc.perform(delete("/transacao"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(get("/estatistica"))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.sum").value(0.0));
    }

    // ---------- GET /estatistica ----------

    @Test
    void getEstatisticaSemTransacoesRetornaZeros() throws Exception {
        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"count\":0,\"sum\":0,\"avg\":0,\"min\":0,\"max\":0}", true));
    }

    @Test
    void getEstatisticaConsideraApenasOsUltimos60Segundos() throws Exception {
        postTransacao(json(10.0, quoted(secondsAgo(1)))).andExpect(status().isCreated());
        postTransacao(json(20.0, quoted(secondsAgo(10)))).andExpect(status().isCreated());
        postTransacao(json(30.5, quoted(secondsAgo(30)))).andExpect(status().isCreated());
        postTransacao(json(1000.0, quoted(secondsAgo(90)))).andExpect(status().isCreated());   // fora da janela
        postTransacao(json(5000.0, "\"2020-08-07T12:34:56.789-03:00\"")).andExpect(status().isCreated()); // fora

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.sum").value(60.5))
                .andExpect(jsonPath("$.avg").value(closeTo(20.1666, 0.001)))
                .andExpect(jsonPath("$.min").value(10.0))
                .andExpect(jsonPath("$.max").value(30.5));
    }

    // ---------- Extras: observabilidade e documentacao ----------

    @Test
    void healthcheckEstaDisponivel() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void metricaDeTempoDeCalculoEstaDisponivel() throws Exception {
        mockMvc.perform(get("/estatistica")).andExpect(status().isOk());

        mockMvc.perform(get("/actuator/metrics/estatistica.calculo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("estatistica.calculo"));
    }

    @Test
    void documentacaoOpenApiEstaDisponivel() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/transacao'].post").exists())
                .andExpect(jsonPath("$.paths['/transacao'].delete").exists())
                .andExpect(jsonPath("$.paths['/estatistica'].get").exists());
    }
}
