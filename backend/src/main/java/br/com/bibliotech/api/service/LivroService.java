package br.com.bibliotech.api.service;

import br.com.bibliotech.api.dto.LivroRequest;
import br.com.bibliotech.api.exception.IsbnDuplicadoException;
import br.com.bibliotech.api.exception.LivroNaoEncontradoException;
import br.com.bibliotech.api.model.Livro;
import br.com.bibliotech.api.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LivroService {
    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Livro> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Livro buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    public Livro criar(LivroRequest dados) {
        String isbn = normalizar(dados.isbn());
        if (repository.findByIsbnIgnoreCase(isbn).isPresent()) {
            throw new IsbnDuplicadoException();
        }
        return repository.save(new Livro(normalizar(dados.titulo()), normalizar(dados.autor()), isbn,
                normalizar(dados.genero()), dados.exemplares()));
    }

    public Livro atualizar(Long id, LivroRequest dados) {
        Livro livro = buscar(id);
        String isbn = normalizar(dados.isbn());
        if (repository.existsByIsbnIgnoreCaseAndIdNot(isbn, id)) {
            throw new IsbnDuplicadoException();
        }
        livro.atualizar(normalizar(dados.titulo()), normalizar(dados.autor()), isbn,
                normalizar(dados.genero()), dados.exemplares());
        return repository.save(livro);
    }

    public void excluir(Long id) {
        Livro livro = buscar(id);
        repository.delete(livro);
    }

    private String normalizar(String valor) {
        return valor.trim();
    }
}