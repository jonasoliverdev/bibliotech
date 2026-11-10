package br.com.bibliotech.api.exception;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(Long id) {
        super("Livro com id " + id + " não foi encontrado.");
    }
}