package tiago.reiz.desafioitau.adapters.in.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Extra "Configuracoes": a janela de tempo das estatisticas pode ser alterada por propriedade. */
@SpringBootTest(properties = "estatistica.janela-segundos=120")
@AutoConfigureMockMvc
class ConfigurableWindowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void janelaDe120SegundosIncluiTransacaoDe90SegundosAtras() throws Exception {
        mockMvc.perform(delete("/transacao")).andExpect(status().isOk());

        postAt(OffsetDateTime.now().minusSeconds(90), 10.0);
        postAt(OffsetDateTime.now().minusSeconds(150), 99.0);

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.sum").value(10.0));
    }

    private void postAt(OffsetDateTime dataHora, double valor) throws Exception {
        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\": %s, \"dataHora\": \"%s\"}".formatted(valor, dataHora)))
                .andExpect(status().isCreated());
    }
}
