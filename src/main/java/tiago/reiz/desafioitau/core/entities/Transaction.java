package tiago.reiz.desafioitau.core.entities;

import java.time.LocalDateTime;

public class Transaction {
    private double value;
    private LocalDateTime dataHora;

    public Transaction() {
    }

    public Transaction(double value, LocalDateTime dataHora) {
        this.value = value;
        this.dataHora = dataHora;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
