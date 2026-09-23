package tiago.reiz.desafioitau.adapters.in.controllers.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Traduz erros para os status exigidos pelo enunciado.
 *
 * <ul>
 *   <li>{@code 422 Unprocessable Entity} sem corpo: JSON valido, mas a transacao viola alguma
 *       regra de aceite (campo ausente, valor negativo, data no futuro).</li>
 *   <li>{@code 400 Bad Request} sem corpo: a API nao compreendeu a requisicao
 *       (JSON malformado, tipos incompativeis, data fora do padrao ISO 8601, corpo vazio).</li>
 * </ul>
 *
 * <p>O enunciado exige respostas <strong>sem corpo</strong>; por isso o motivo de cada erro
 * e registrado em log (extra "Logs"/"Tratamento de Erros") em vez de ser devolvido ao cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidationErrors(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        log.warn("Transacao rejeitada (422): {}", erros);
        return ResponseEntity.unprocessableContent().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.warn("Requisicao nao compreendida (400): {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().build();
    }
}
