(() => {
  const API_URL = window.BIBLIOTECH_API_URL || 'http://localhost:8080/api/livros';
  const form = document.querySelector('#book-form');
  const tbody = document.querySelector('#book-list');
  const notice = document.querySelector('#notice');
  const count = document.querySelector('#book-count');
  const search = document.querySelector('#search');
  const loading = document.querySelector('#loading');
  const empty = document.querySelector('#empty');
  const submitButton = document.querySelector('#submit-button');
  const cancelEdit = document.querySelector('#cancel-edit');
  const dialog = document.querySelector('#delete-dialog');
  let books = [];
  let editingId = null;
  let deletingId = null;

  function showNotice(message, type = 'info') {
    notice.textContent = message;
    notice.className = `notice notice-${type}`;
    notice.hidden = false;
  }

  function clearNotice() { notice.hidden = true; notice.textContent = ''; }

  async function api(path = '', options = {}) {
    let response;
    try {
      response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers }
      });
    } catch {
      throw new Error('Não foi possível conectar à API. Verifique se o back-end está em execução.');
    }
    if (response.status === 204) return null;
    const payload = await response.json().catch(() => ({}));
    if (!response.ok) {
      const details = payload.detalhes ? Object.values(payload.detalhes).join(' ') : '';
      throw new Error([payload.mensagem, details].filter(Boolean).join(' ') || `A API retornou erro ${response.status}.`);
    }
    return payload;
  }

  function updateCount(total) {
    count.textContent = `${total} ${total === 1 ? 'livro' : 'livros'}`;
  }

  function createCell(text, className = '') {
    const td = document.createElement('td');
    td.textContent = text ?? '';
    if (className) td.className = className;
    return td;
  }

  function renderBooks() {
    const term = search.value.trim().toLocaleLowerCase('pt-BR');
    const filtered = books.filter(book => [book.titulo, book.autor, book.isbn, book.genero]
      .some(value => String(value ?? '').toLocaleLowerCase('pt-BR').includes(term)));
    tbody.replaceChildren();
    filtered.forEach(book => {
      const row = document.createElement('tr');
      const identity = document.createElement('td');
      identity.className = 'book-identity';
      const title = document.createElement('strong');
      title.textContent = book.titulo;
      const author = document.createElement('span');
      author.textContent = book.autor;
      identity.append(title, author);
      row.append(identity, createCell(book.isbn, 'isbn-cell'), createCell(book.genero), createCell(String(book.exemplares)));
      const actions = document.createElement('td');
      actions.className = 'row-actions';
      const edit = document.createElement('button');
      edit.type = 'button'; edit.className = 'icon-button'; edit.textContent = 'Editar';
      edit.setAttribute('aria-label', `Editar ${book.titulo}`);
      edit.addEventListener('click', () => startEdit(book));
      const remove = document.createElement('button');
      remove.type = 'button'; remove.className = 'icon-button icon-danger'; remove.textContent = 'Excluir';
      remove.setAttribute('aria-label', `Excluir ${book.titulo}`);
      remove.addEventListener('click', () => askDelete(book));
      actions.append(edit, remove);
      row.append(actions);
      tbody.append(row);
    });
    updateCount(books.length);
    empty.hidden = books.length !== 0;
    empty.querySelector('strong').textContent = books.length === 0 && term ? 'Nenhum resultado encontrado' : 'Nenhum livro por aqui ainda';
    empty.querySelector('span:last-child').textContent = books.length === 0 && term ? 'Tente buscar por outro termo.' : 'Use o formulário ao lado para adicionar o primeiro título.';
    empty.hidden = filtered.length !== 0;
  }

  async function loadBooks() {
    clearNotice(); loading.hidden = false;
    try {
      books = await api();
      renderBooks();
    } catch (error) {
      books = []; renderBooks();
      showNotice(error.message, 'error');
    } finally {
      loading.hidden = true;
    }
  }

  function resetForm() {
    editingId = null;
    form.reset();
    document.querySelector('#exemplares').value = '0';
    document.querySelector('#form-eyebrow').textContent = 'NOVO REGISTRO';
    document.querySelector('#form-title').textContent = 'Adicionar livro';
    document.querySelector('.form-intro').textContent = 'Preencha os dados para incluir um título no acervo.';
    submitButton.textContent = 'Salvar livro';
    cancelEdit.hidden = true;
  }

  function startEdit(book) {
    editingId = book.id;
    for (const field of ['titulo', 'autor', 'isbn', 'genero', 'exemplares']) {
      form.elements[field].value = book[field];
    }
    document.querySelector('#form-eyebrow').textContent = 'EDITANDO REGISTRO';
    document.querySelector('#form-title').textContent = 'Atualizar livro';
    document.querySelector('.form-intro').textContent = `Editando “${book.titulo}”.`;
    submitButton.textContent = 'Salvar alterações';
    cancelEdit.hidden = false;
    document.querySelector('#form-panel').scrollIntoView({ behavior: 'smooth', block: 'start' });
    document.querySelector('#titulo').focus({ preventScroll: true });
  }

  function askDelete(book) {
    deletingId = book.id;
    dialog.querySelector('h2').textContent = `Remover “${book.titulo}”?`;
    dialog.showModal();
  }

  form.addEventListener('submit', async event => {
    event.preventDefault(); clearNotice();
    const data = Object.fromEntries(new FormData(form).entries());
    data.titulo = data.titulo.trim(); data.autor = data.autor.trim();
    data.isbn = data.isbn.trim(); data.genero = data.genero.trim();
    data.exemplares = Number(data.exemplares);
    if (!form.reportValidity()) return;
    if (!Number.isInteger(data.exemplares) || data.exemplares < 0) {
      showNotice('Exemplares deve ser um número inteiro igual ou maior que zero.', 'error'); return;
    }
    submitButton.disabled = true;
    try {
      await api(editingId === null ? '' : `/${editingId}`, {
        method: editingId === null ? 'POST' : 'PUT', body: JSON.stringify(data)
      });
      showNotice(editingId === null ? 'Livro cadastrado com sucesso.' : 'Livro atualizado com sucesso.', 'success');
      resetForm(); await loadBooks();
    } catch (error) {
      showNotice(error.message, 'error');
    } finally { submitButton.disabled = false; }
  });

  document.querySelector('#confirm-delete').addEventListener('click', async event => {
    event.preventDefault();
    if (deletingId === null) return;
    const id = deletingId; deletingId = null; dialog.close();
    try {
      await api(`/${id}`, { method: 'DELETE' });
      if (editingId === id) resetForm();
      showNotice('Livro excluído com sucesso.', 'success'); await loadBooks();
    } catch (error) { showNotice(error.message, 'error'); }
  });

  cancelEdit.addEventListener('click', resetForm);
  document.querySelector('#refresh').addEventListener('click', loadBooks);
  search.addEventListener('input', renderBooks);
  loadBooks();
})();