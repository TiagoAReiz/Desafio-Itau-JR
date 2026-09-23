package tiago.reiz.desafioitau.core.entities;

import java.util.DoubleSummaryStatistics;

/**
 * Estatisticas das transacoes dentro da janela de tempo.
 * Quando nao ha transacoes, todos os campos valem {@code 0}, conforme o enunciado.
 */
public record Statistics(long count, double sum, double avg, double min, double max) {

    public static final Statistics EMPTY = new Statistics(0, 0, 0, 0, 0);

    public static Statistics from(DoubleSummaryStatistics summary) {
        if (summary.getCount() == 0) {
            return EMPTY;
        }
        return new Statistics(
                summary.getCount(),
                summary.getSum(),
                summary.getAverage(),
                summary.getMin(),
                summary.getMax());
    }
}
