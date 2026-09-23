package tiago.reiz.desafioitau.core.entities;

import org.junit.jupiter.api.Test;

import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsTest {

    @Test
    void semTransacoesTodosOsCamposSaoZero() {
        Statistics statistics = Statistics.from(new DoubleSummaryStatistics());

        // DoubleSummaryStatistics vazio retorna +/-Infinity para min/max; o enunciado exige 0
        assertThat(statistics).isEqualTo(new Statistics(0, 0, 0, 0, 0));
        assertThat(statistics).isSameAs(Statistics.EMPTY);
    }

    @Test
    void calculaCountSumAvgMinMax() {
        Statistics statistics = Statistics.from(DoubleStream.of(10.0, 20.0, 30.5).summaryStatistics());

        assertThat(statistics.count()).isEqualTo(3);
        assertThat(statistics.sum()).isEqualTo(60.5);
        assertThat(statistics.avg()).isEqualTo(60.5 / 3);
        assertThat(statistics.min()).isEqualTo(10.0);
        assertThat(statistics.max()).isEqualTo(30.5);
    }
}
