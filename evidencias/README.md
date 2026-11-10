# Evidências da atividade — roteiro para capturar

Os testes MVC automatizados ficam em `backend/src/test/java/br/com/bibliotech/api/controller/LivroControllerTest.java` e podem ser executados com `mvn test` na pasta `backend/`. Eles validam a camada HTTP com o serviço simulado; não são evidência de persistência no MySQL. A coleção `../postman/BiblioTech.evidencias.postman_collection.json` executa um fluxo CRUD real, usa ISBN exclusivo e apaga o livro de teste no final. Pode ser importada no Postman Desktop ou executada com Newman (`npx newman run ../postman/BiblioTech.evidencias.postman_collection.json`).

Execuções registradas: `mvn test` — 7 testes aprovados, 0 falhas e 0 erros; Newman — 9 requisições e 11 assertions aprovadas contra a API e o banco locais. O arquivo `02-postman-newman.txt` registra a saída do executor oficial de coleções Postman. O Postman Desktop deste computador exige autenticação para abrir a interface; por isso a evidência de API foi capturada via Newman, sem simular uma tela do Postman. Para gerar capturas visuais da interface Desktop, faça login no Postman e importe a coleção indicada acima.

1. **Postman — CREATE e leitura:** resposta `201 Created` do `POST /api/livros`, seguida do livro visível em `GET /api/livros`.
2. **Postman — validação:** resposta `400` com detalhes para título inválido/cópias negativas.
3. **Postman — regras:** `409` ao repetir ISBN e `404` ao consultar ID inexistente.
4. **Postman — UPDATE e DELETE:** resposta `200` da edição e `204` da exclusão; confirme que uma nova listagem não mostra o registro excluído.
5. **MySQL Workbench/cliente SQL:** execute `SELECT id, titulo, autor, isbn, genero, exemplares FROM bibliotech.livros;` após criar, atualizar e excluir. Capture o resultado real.
6. **Front-end:** captura da página com livros listados e do formulário; teste cadastrar, editar, excluir com confirmação e mensagem de erro.

Nome sugerido: `01-postman-criar.png`, `02-postman-validacao.png`, `03-postman-conflito-nao-encontrado.png`, `04-postman-editar-excluir.png`, `05-mysql-select.png`, `06-frontend-crud.png`. Não inclua senhas pessoais nas imagens. A coleção de requisições está em `../postman/BiblioTech.postman_collection.json`.