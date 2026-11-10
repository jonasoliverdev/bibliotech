package br.com.bibliotech.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException exception) {
        Map<String, String> detalhes = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erro ->
                detalhes.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, "Verifique os dados enviados.", detalhes);
    }

    @ExceptionHandler(LivroNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(LivroNaoEncontradoException exception) {
        return resposta(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(IsbnDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> tratarIsbnDuplicado(IsbnDuplicadoException exception) {
        return resposta(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarRestricaoBanco(DataIntegrityViolationException exception) {
        return resposta(HttpStatus.CONFLICT, "Conflito com uma regra de unicidade do banco de dados.", Map.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarJsonInvalido(HttpMessageNotReadableException exception) {
        return resposta(HttpStatus.BAD_REQUEST, "O corpo da requisição está ausente ou não contém JSON válido.", Map.of());
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem, Map<String, String> detalhes) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        if (!detalhes.isEmpty()) corpo.put("detalhes", detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}