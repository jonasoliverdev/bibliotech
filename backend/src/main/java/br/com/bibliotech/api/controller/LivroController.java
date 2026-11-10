package br.com.bibliotech.api.controller;

import br.com.bibliotech.api.dto.LivroRequest;
import br.com.bibliotech.api.dto.LivroResponse;
import br.com.bibliotech.api.model.Livro;
import br.com.bibliotech.api.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5173"})
public class LivroController {
    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest dados) {
        Livro salvo = service.criar(dados);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(localizacao).body(LivroResponse.de(salvo));
    }

    @GetMapping
    public List<LivroResponse> listar() {
        return service.listar().stream().map(LivroResponse::de).toList();
    }

    @GetMapping("/{id}")
    public LivroResponse buscar(@PathVariable Long id) {
        return LivroResponse.de(service.buscar(id));
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest dados) {
        return LivroResponse.de(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}