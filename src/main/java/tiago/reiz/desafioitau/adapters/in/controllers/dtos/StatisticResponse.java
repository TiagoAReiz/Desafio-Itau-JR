package tiago.reiz.desafioitau.adapters.in.controllers.dtos;

import tiago.reiz.desafioitau.core.entities.Statistics;

/** Corpo da resposta de {@code GET /estatistica}, com os campos exatamente como no enunciado. */
public record StatisticResponse(long count, double sum, double avg, double min, double max) {

    public static StatisticResponse from(Statistics statistics) {
        return new StatisticResponse(
                statistics.count(),
                statistics.sum(),
                statistics.avg(),
                statistics.min(),
                statistics.max());
    }
}
