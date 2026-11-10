package br.com.bibliotech.api.exception;

public class IsbnDuplicadoException extends RuntimeException {
    public IsbnDuplicadoException() {
        super("Já existe um livro cadastrado com esse ISBN.");
    }
}