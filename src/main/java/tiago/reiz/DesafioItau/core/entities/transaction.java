package tiago.reiz.DesafioItau.core.entities;

import java.time.LocalDateTime;

public class transaction {
    private double value;
    private LocalDateTime dataHora;

    public transaction() {
    }

    public transaction(double value, LocalDateTime dataHora) {
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
