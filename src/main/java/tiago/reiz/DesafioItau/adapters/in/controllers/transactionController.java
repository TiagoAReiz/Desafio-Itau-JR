package tiago.reiz.DesafioItau.adapters.in.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.statistic;
import tiago.reiz.DesafioItau.adapters.in.controllers.dtos.transactionRequest;
import tiago.reiz.DesafioItau.core.interfaces.transactionService;

import java.util.List;

@RestController
@RequestMapping
public class transactionController {
    private final transactionService service;
    transactionController(transactionService service){
        this.service = service;
    }

    @PostMapping("transacao")
    public ResponseEntity<Void> createTransaction(@RequestBody @Valid transactionRequest transaction){

        service.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();



    }

    @GetMapping("estatisticas")
    public ResponseEntity<statistic> getStatistics(){
        return ResponseEntity.ok(service.getStatistics());
    }

    @DeleteMapping("transacoes")
    public ResponseEntity<Void> deleteAllTransactions(){

            service.deleteAll();
            return ResponseEntity.ok().build();


    }

}
