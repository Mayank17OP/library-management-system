(function () {
  const KEY = 'library-management-system:v1';
  const DAY = 24 * 60 * 60 * 1000;
  const $ = (id) => document.getElementById(id);
  const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

  let db = load();

  function load() {
    try {
      const data = JSON.parse(localStorage.getItem(KEY));
      if (data && Array.isArray(data.books)) return data;
    } catch (e) { /* ignore corrupt data */ }
    return { books: [], members: [], loans: [], fineRate: 5 };
  }
  function save() { localStorage.setItem(KEY, JSON.stringify(db)); }
  const uid = () => Date.now().toString(36) + Math.random().toString(36).slice(2, 7);

  // ---- Helpers ----
  function activeLoans(bookId) { return db.loans.filter((l) => l.bookId === bookId && !l.returnedAt).length; }
  function loansOfMember(id) { return db.loans.filter((l) => l.memberId === id && !l.returnedAt).length; }
  function fineFor(loan, now = Date.now()) {
    const end = loan.returnedAt || now;
    const overdueDays = Math.floor((end - loan.dueAt) / DAY);
    return overdueDays > 0 ? overdueDays * db.fineRate : 0;
  }

  // ---- Tabs ----
  document.querySelectorAll('nav button').forEach((btn) => btn.addEventListener('click', () => {
    document.querySelectorAll('nav button').forEach((b) => b.classList.toggle('active', b === btn));
    document.querySelectorAll('.tab').forEach((t) => t.classList.toggle('active', t.id === btn.dataset.tab));
  }));

  // ---- Books ----
  function renderBooks() {
    const q = $('book-search').value.trim().toLowerCase();
    const books = db.books.filter((b) => !q || [b.title, b.author, b.isbn].some((v) => v.toLowerCase().includes(q)));
    $('book-rows').innerHTML = books.length ? books.map((b) => {
      const out = activeLoans(b.id);
      return `<tr><td>${esc(b.title)}</td><td>${esc(b.author)}</td><td>${esc(b.isbn)}</td>
        <td>${b.copies - out} / ${b.copies}</td>
        <td><div class="actions">
          <button class="small" data-act="edit-book" data-id="${b.id}">Edit</button>
          <button class="small danger" data-act="del-book" data-id="${b.id}">Delete</button>
        </div></td></tr>`;
    }).join('') : '<tr><td colspan="5" class="empty">No books found.</td></tr>';
    refreshSelects();
  }
  function resetBookForm() {
    $('book-form').reset();
    $('book-id').value = '';
    $('book-copies').value = 1;
    $('book-cancel').classList.add('hidden');
  }
  $('book-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const id = $('book-id').value;
    const copies = Math.max(1, parseInt($('book-copies').value, 10) || 1);
    const data = {
      title: $('book-title').value.trim(), author: $('book-author').value.trim(),
      isbn: $('book-isbn').value.trim(), copies,
    };
    if (id) {
      const b = db.books.find((x) => x.id === id);
      if (b) {
        if (copies < activeLoans(id)) { alert('Cannot set fewer copies than are currently on loan.'); return; }
        Object.assign(b, data);
      }
    } else {
      db.books.push({ id: uid(), ...data });
    }
    save(); resetBookForm(); renderAll();
  });
  $('book-cancel').addEventListener('click', resetBookForm);
  $('book-search').addEventListener('input', renderBooks);
  $('book-rows').addEventListener('click', (e) => {
    const btn = e.target.closest('button'); if (!btn) return;
    const id = btn.dataset.id;
    if (btn.dataset.act === 'edit-book') {
      const b = db.books.find((x) => x.id === id); if (!b) return;
      $('book-id').value = b.id; $('book-title').value = b.title; $('book-author').value = b.author;
      $('book-isbn').value = b.isbn; $('book-copies').value = b.copies;
      $('book-cancel').classList.remove('hidden'); $('book-title').focus();
    } else if (btn.dataset.act === 'del-book') {
      if (activeLoans(id) > 0) { alert('This book is currently on loan and cannot be deleted.'); return; }
      if (!confirm('Delete this book?')) return;
      db.books = db.books.filter((b) => b.id !== id);
      save(); renderAll();
    }
  });

  // ---- Members ----
  function renderMembers() {
    $('member-rows').innerHTML = db.members.length ? db.members.map((m) => `
      <tr><td>${esc(m.name)}</td><td>${esc(m.email)}</td><td>${loansOfMember(m.id)}</td>
      <td><div class="actions"><button class="small danger" data-act="del-member" data-id="${m.id}">Remove</button></div></td></tr>`).join('')
      : '<tr><td colspan="4" class="empty">No members yet.</td></tr>';
    refreshSelects();
  }
  $('member-form').addEventListener('submit', (e) => {
    e.preventDefault();
    db.members.push({ id: uid(), name: $('member-name').value.trim(), email: $('member-email').value.trim() });
    save(); e.target.reset(); renderAll();
  });
  $('member-rows').addEventListener('click', (e) => {
    const btn = e.target.closest('button'); if (!btn) return;
    const id = btn.dataset.id;
    if (loansOfMember(id) > 0) { alert('This member still has books checked out.'); return; }
    if (!confirm('Remove this member?')) return;
    db.members = db.members.filter((m) => m.id !== id);
    save(); renderAll();
  });

  // ---- Loans ----
  function refreshSelects() {
    const mSel = $('loan-member'), bSel = $('loan-book');
    const curM = mSel.value, curB = bSel.value;
    mSel.innerHTML = '<option value="">Select member…</option>' + db.members.map((m) => `<option value="${m.id}">${esc(m.name)}</option>`).join('');
    bSel.innerHTML = '<option value="">Select book…</option>' + db.books
      .filter((b) => b.copies - activeLoans(b.id) > 0)
      .map((b) => `<option value="${b.id}">${esc(b.title)} (${b.copies - activeLoans(b.id)} free)</option>`).join('');
    if ([...mSel.options].some((o) => o.value === curM)) mSel.value = curM;
    if ([...bSel.options].some((o) => o.value === curB)) bSel.value = curB;
  }
  function renderLoans() {
    const now = Date.now();
    const rows = [...db.loans].sort((a, b) => (a.returnedAt ? 1 : 0) - (b.returnedAt ? 1 : 0) || a.dueAt - b.dueAt);
    let total = 0;
    $('loan-rows').innerHTML = rows.length ? rows.map((l) => {
      const m = db.members.find((x) => x.id === l.memberId), b = db.books.find((x) => x.id === l.bookId);
      const fine = fineFor(l, now); total += fine;
      const overdue = !l.returnedAt && now > l.dueAt;
      const status = l.returnedAt ? `Returned ${new Date(l.returnedAt).toLocaleDateString()}`
        : overdue ? '<span class="overdue">Overdue</span>' : 'On loan';
      const action = l.returnedAt ? '' : `<button class="small" data-act="return" data-id="${l.id}">Return</button>`;
      return `<tr><td>${esc(m ? m.name : '(removed)')}</td><td>${esc(b ? b.title : '(removed)')}</td>
        <td>${new Date(l.dueAt).toLocaleDateString()}</td><td>${status}</td><td>${fine}</td>
        <td><div class="actions">${action}</div></td></tr>`;
    }).join('') : '<tr><td colspan="6" class="empty">No loans yet.</td></tr>';
    $('total-fines').textContent = `Total fines shown: ₹${total}`;
  }
  $('loan-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const memberId = $('loan-member').value, bookId = $('loan-book').value;
    const days = Math.max(1, parseInt($('loan-days').value, 10) || 14);
    if (!memberId || !bookId) return;
    const book = db.books.find((b) => b.id === bookId);
    if (!book || activeLoans(bookId) >= book.copies) { alert('No copies available.'); return; }
    db.loans.push({ id: uid(), memberId, bookId, borrowedAt: Date.now(), dueAt: Date.now() + days * DAY, returnedAt: null });
    save(); renderAll();
  });
  $('loan-rows').addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-act="return"]'); if (!btn) return;
    const loan = db.loans.find((l) => l.id === btn.dataset.id); if (!loan) return;
    loan.returnedAt = Date.now();
    const fine = fineFor(loan);
    if (fine > 0) alert(`Returned late. Fine: ₹${fine}`);
    save(); renderAll();
  });
  $('fine-rate').value = db.fineRate;
  $('fine-rate').addEventListener('change', (e) => {
    db.fineRate = Math.max(0, parseFloat(e.target.value) || 0); save(); renderLoans();
  });

  function renderAll() { renderBooks(); renderMembers(); refreshSelects(); renderLoans(); }
  renderAll();
  setInterval(renderLoans, 60 * 1000); // keep overdue status/fines fresh
})();
