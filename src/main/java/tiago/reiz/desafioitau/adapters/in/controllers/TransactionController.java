package tiago.reiz.desafioitau.adapters.in.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.StatisticResponse;
import tiago.reiz.desafioitau.adapters.in.controllers.dtos.TransactionRequest;
import tiago.reiz.desafioitau.core.interfaces.TransactionService;

import java.util.List;

@RestController
@RequestMapping
public class TransactionController {
    private final TransactionService service;
    TransactionController(TransactionService service){
        this.service = service;
    }

    @PostMapping("transacao")
    public ResponseEntity<Void> createTransaction(@RequestBody @Valid TransactionRequest transaction){

        service.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();



    }

    @GetMapping("estatisticas")
    public ResponseEntity<StatisticResponse> getStatistics(){
        return ResponseEntity.ok(service.getStatistics());
    }

    @DeleteMapping("transacoes")
    public ResponseEntity<Void> deleteAllTransactions(){

            service.deleteAll();
            return ResponseEntity.ok().build();


    }

}
