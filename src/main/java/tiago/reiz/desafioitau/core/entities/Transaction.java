package tiago.reiz.desafioitau.core.entities;

import java.time.OffsetDateTime;

/**
 * Transacao registrada: um valor e o instante (com fuso horario) em que ocorreu.
 * Imutavel, o que a torna segura para compartilhamento entre threads.
 */
public record Transaction(double valor, OffsetDateTime dataHora) {
}
