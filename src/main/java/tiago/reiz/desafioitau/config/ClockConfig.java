package tiago.reiz.desafioitau.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Expoe um {@link Clock} como bean para que o "agora" seja injetavel
 * (e controlavel nos testes da janela de tempo).
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
