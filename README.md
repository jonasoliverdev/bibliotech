# BiblioTech — Sistema de Acervo de Biblioteca

Aplicação de demonstração para cadastro e gerenciamento de livros. O repositório está separado em `backend/` (Java 17+, Spring Boot 3, API REST e JPA), `frontend/` (HTML, CSS e JavaScript sem dependências) e `database/` (MySQL em Docker ou instalação local).

## Requisitos

- JDK 17 ou superior (o código foi preparado para Spring Boot 3).
- Maven 3.8+.
- MySQL 5.7+ **ou** Docker Desktop com Compose (o Compose usa MySQL 8.4).
- Um navegador moderno. Para servir o front-end, Python 3 ou Node.js pode ser usado.

## Inicialização rápida no Windows (PowerShell)

1. Abra o terminal nesta pasta do projeto.
2. Inicie o banco: `docker compose up -d mysql` (ou use um MySQL local já em execução).
3. Entre em `backend/` e inicie a API: `mvn spring-boot:run`.
4. Em outro terminal, entre em `frontend/` e sirva a página: `py -m http.server 5500` (ou `python -m http.server 5500`).
5. Acesse `http://localhost:5500`. A API fica em `http://localhost:8080/api/livros`.

O Compose cria o banco `bibliotech`, usuário `bibliotech` e senha local de desenvolvimento `bibliotech_dev`. Altere essas credenciais antes de qualquer uso compartilhado. O Spring cria/atualiza a tabela `livros` com JPA.

### MySQL já instalado

Crie o banco com `database/init.sql` (a collation usada é compatível com MySQL 5.7 e 8). Configure as variáveis de ambiente antes de iniciar o Spring Boot; exemplos para PowerShell:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/bibliotech?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME = 'seu_usuario'
$env:DB_PASSWORD = 'sua_senha'
Set-Location backend
mvn spring-boot:run
```

Se `mvn` não for reconhecido, use o Maven configurado no IntelliJ IDEA (janela Maven, objetivo `spring-boot:run`) ou adicione o Maven ao `PATH`. Ao iniciar pela IDE, configure as mesmas variáveis de ambiente na configuração de execução da aplicação. O arquivo `backend/src/main/resources/application.properties` contém os valores padrão locais, que podem ser substituídos por essas variáveis.

## Contrato da API

Todos os caminhos são relativos a `/api`. Os corpos e respostas usam JSON. Para cadastrar ou atualizar, envie `titulo`, `autor`, `isbn`, `genero` e `exemplares`.

| Método e caminho | Resultado | Respostas principais |
|---|---|---|
| `POST /livros` | Cadastra livro | `201 Created` e representação do livro; `400` validação; `409` ISBN duplicado |
| `GET /livros` | Lista livros | `200 OK` e array JSON |
| `GET /livros/{id}` | Busca por ID | `200 OK`; `404 Not Found` |
| `PUT /livros/{id}` | Substitui os campos editáveis | `200 OK`; `400`, `404` ou `409` |
| `DELETE /livros/{id}` | Exclui | `204 No Content`; `404 Not Found` |

Exemplo de JSON:

```json
{
  "titulo": "O Hobbit",
  "autor": "J. R. R. Tolkien",
  "isbn": "9788595084742",
  "genero": "Fantasia",
  "exemplares": 4
}
```

O identificador é gerado pelo banco e não deve ser enviado. Título deve ter de 3 a 150 caracteres; os campos textuais são obrigatórios e exemplares deve ser inteiro maior ou igual a zero. Um ISBN não pode ser repetido. Erros retornam objeto JSON com `status`, `erro`, `mensagem` e, quando aplicável, `detalhes` por campo.

## Estrutura e materiais

- `backend/`: aplicação Spring Boot organizada em controller, service, repository, model, DTO e handler de erros.
- `frontend/`: interface de listagem, cadastro, edição e exclusão com confirmação.
- `database/`: inicialização do banco e Compose local.
- `postman/BiblioTech.postman_collection.json`: solicitações para os cinco endpoints.
- `RESPOTAS.txt`: respostas reflexivas e registro dos prompts usados.
- `evidencias/README.md`: roteiro para capturar evidências reais após executar o projeto.

## Validação e evidências

Execute os testes automatizados da API em `backend/` com `mvn test` (ou com o Maven configurado no IntelliJ IDEA). O JavaScript pode ser verificado com `node --check frontend/app.js`. A API foi empacotada com o Maven incluído no IntelliJ IDEA, e o usuário confirmou que conseguiu executar o sistema e consultar os dados no MySQL. Os testes MVC cobrem os status e respostas principais do contrato, usando o serviço simulado; eles não substituem a verificação de integração com MySQL. As capturas reais do Postman, banco e interface ainda devem ser feitas seguindo `evidencias/README.md`.