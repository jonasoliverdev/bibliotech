package br.com.bibliotech.api.repository;

import br.com.bibliotech.api.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByIsbnIgnoreCase(String isbn);
    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, Long id);
}