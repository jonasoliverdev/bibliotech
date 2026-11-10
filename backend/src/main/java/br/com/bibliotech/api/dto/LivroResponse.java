package br.com.bibliotech.api.dto;

import br.com.bibliotech.api.model.Livro;

public record LivroResponse(Long id, String titulo, String autor, String isbn, String genero, Integer exemplares) {
    public static LivroResponse de(Livro livro) {
        return new LivroResponse(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getIsbn(),
                livro.getGenero(), livro.getExemplares());
    }
}