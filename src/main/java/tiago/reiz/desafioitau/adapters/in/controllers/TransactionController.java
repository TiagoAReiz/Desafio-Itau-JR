package tiago.reiz.desafioitau.adapters.in.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.StatisticResponse;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.TransactionRequest;
import tiago.reiz.desafioitau.core.interfaces.TransactionService;

@RestController
@Tag(name = "Transacoes", description = "Registro de transacoes e calculo de estatisticas")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @Operation(summary = "Registra uma transacao",
            description = "Aceita apenas transacoes com valor >= 0 e dataHora (ISO 8601) que nao esteja no futuro.")
    @ApiResponse(responseCode = "201", description = "Transacao aceita e registrada", content = @Content)
    @ApiResponse(responseCode = "422", description = "Transacao recusada (campo ausente, valor negativo ou data no futuro)",
            content = @Content)
    @ApiResponse(responseCode = "400", description = "Requisicao nao compreendida (ex.: JSON invalido)", content = @Content)
    @PostMapping(path = "/transacao", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTransaction(@RequestBody @Valid TransactionRequest request) {
        service.register(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Apaga todas as transacoes")
    @ApiResponse(responseCode = "200", description = "Todas as transacoes foram apagadas", content = @Content)
    @DeleteMapping("/transacao")
    public ResponseEntity<Void> deleteAllTransactions() {
        service.deleteAll();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Estatisticas das transacoes na janela de tempo",
            description = "Retorna count, sum, avg, min e max das transacoes dos ultimos 60 segundos (configuravel). "
                    + "Sem transacoes na janela, todos os valores sao 0.")
    @ApiResponse(responseCode = "200", description = "Estatisticas calculadas")
    @GetMapping(path = "/estatistica", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatisticResponse> getStatistics() {
        return ResponseEntity.ok(StatisticResponse.from(service.getStatistics()));
    }
}
