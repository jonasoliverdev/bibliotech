package br.com.bibliotech.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank(message = "O título é obrigatório.")
        @Size(min = 3, max = 150, message = "O título deve ter entre 3 e 150 caracteres.")
        String titulo,
        @NotBlank(message = "O autor é obrigatório.")
        @Size(max = 150, message = "O autor deve ter no máximo 150 caracteres.")
        String autor,
        @NotBlank(message = "O ISBN é obrigatório.")
        @Size(max = 40, message = "O ISBN deve ter no máximo 40 caracteres.")
        String isbn,
        @NotBlank(message = "O gênero é obrigatório.")
        @Size(max = 100, message = "O gênero deve ter no máximo 100 caracteres.")
        String genero,
        @NotNull(message = "A quantidade de exemplares é obrigatória.")
        @Min(value = 0, message = "A quantidade de exemplares não pode ser negativa.")
        Integer exemplares
) { }