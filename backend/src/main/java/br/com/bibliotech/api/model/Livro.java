package br.com.bibliotech.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "livros", uniqueConstraints = @UniqueConstraint(name = "uk_livros_isbn", columnNames = "isbn"))
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 150)
    private String autor;

    @Column(nullable = false, length = 40)
    private String isbn;

    @Column(nullable = false, length = 100)
    private String genero;

    @Column(nullable = false)
    private Integer exemplares;

    protected Livro() { }

    public Livro(String titulo, String autor, String isbn, String genero, Integer exemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.exemplares = exemplares;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public String getGenero() { return genero; }
    public Integer getExemplares() { return exemplares; }

    public void atualizar(String titulo, String autor, String isbn, String genero, Integer exemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.exemplares = exemplares;
    }
}
