package br.com.bibliotech.api.controller;

import br.com.bibliotech.api.dto.LivroRequest;
import br.com.bibliotech.api.exception.ApiExceptionHandler;
import br.com.bibliotech.api.exception.IsbnDuplicadoException;
import br.com.bibliotech.api.exception.LivroNaoEncontradoException;
import br.com.bibliotech.api.model.Livro;
import br.com.bibliotech.api.service.LivroService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LivroControllerTest {
    private static final String LIVRO_JSON = """
            {
              "titulo": "O Hobbit",
              "autor": "J. R. R. Tolkien",
              "isbn": "9788595084742",
              "genero": "Fantasia",
              "exemplares": 4
            }
            """;

    private MockMvc mockMvc;

    private Livro livro;
    private StubLivroService service;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void prepararLivro() {
        livro = new Livro("O Hobbit", "J. R. R. Tolkien", "9788595084742", "Fantasia", 4);
        ReflectionTestUtils.setField(livro, "id", 42L);
        service = new StubLivroService(livro);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new LivroController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void liberarValidador() {
        validator.close();
    }

    @Test
    void criarRetorna201ERepresentacao() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType("application/json")
                        .content(LIVRO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.titulo").value("O Hobbit"));
    }

    @Test
    void listarRetornaLivros() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("9788595084742"));
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        String dadosInvalidos = """
                {
                  "titulo": "x",
                  "autor": "",
                  "isbn": "",
                  "genero": "Teste",
                  "exemplares": -1
                }
                """;

        mockMvc.perform(post("/api/livros")
                        .contentType("application/json")
                        .content(dadosInvalidos))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes.titulo").exists());
    }

    @Test
    void livroAusenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/livros/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void isbnDuplicadoRetorna409() throws Exception {
        service.isbnDuplicado = true;

        mockMvc.perform(post("/api/livros")
                        .contentType("application/json")
                        .content(LIVRO_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void atualizarRetorna200() throws Exception {
        mockMvc.perform(put("/api/livros/42")
                        .contentType("application/json")
                        .content(LIVRO_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void excluirRetorna204() throws Exception {
        mockMvc.perform(delete("/api/livros/42"))
                .andExpect(status().isNoContent());

        assertEquals(42L, service.excluidoId);
    }

    private static class StubLivroService extends LivroService {
        private final Livro livro;
        private boolean isbnDuplicado;
        private Long excluidoId;

        private StubLivroService(Livro livro) {
            super(null);
            this.livro = livro;
        }

        @Override
        public List<Livro> listar() {
            return List.of(livro);
        }

        @Override
        public Livro buscar(Long id) {
            if (!id.equals(livro.getId())) {
                throw new LivroNaoEncontradoException(id);
            }
            return livro;
        }

        @Override
        public Livro criar(LivroRequest dados) {
            if (isbnDuplicado) {
                throw new IsbnDuplicadoException();
            }
            return livro;
        }

        @Override
        public Livro atualizar(Long id, LivroRequest dados) {
            return buscar(id);
        }

        @Override
        public void excluir(Long id) {
            buscar(id);
            excluidoId = id;
        }
    }
}
