package tiago.reiz.desafioitau.adapters.in.controllers;

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
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping(path = "/transacao", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTransaction(@RequestBody @Valid TransactionRequest request) {
        service.register(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/transacao")
    public ResponseEntity<Void> deleteAllTransactions() {
        service.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/estatistica", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatisticResponse> getStatistics() {
        return ResponseEntity.ok(StatisticResponse.from(service.getStatistics()));
    }
}
