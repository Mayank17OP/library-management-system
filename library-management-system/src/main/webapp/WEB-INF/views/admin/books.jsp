<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Books"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Librarian panel</p>
        <h1>Book catalogue</h1>
        <p>Add books, review total and available copies, and search by book ID, title or author.</p>
    </div>
</section>

<div class="container">
    <div class="split section">
        <%-- ------------------------------ ADD BOOK ------------------------------ --%>
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Add a book</h2>
                <span class="badge badge-info">Requirement 2</span>
            </div>
            <form method="post" action="${ctx}/admin/books" data-validate>
                <input type="hidden" name="action" value="add">
                <div class="form-grid">
                    <div class="field">
                        <label for="bookId">Book ID</label>
                        <input type="text" id="bookId" name="bookId" required maxlength="12"
                               placeholder="B013" data-rule="id" data-label="Book ID">
                        <span class="hint">Duplicate IDs are rejected.</span>
                    </div>
                    <div class="field">
                        <label for="totalCopies">Total copies</label>
                        <input type="number" id="totalCopies" name="totalCopies" required min="1" max="999"
                               value="1" data-rule="positive" data-label="Copies">
                    </div>
                    <div class="field" style="grid-column:1/-1">
                        <label for="title">Title</label>
                        <input type="text" id="title" name="title" required maxlength="120"
                               data-rule="name" data-label="Title"
                               placeholder="Introduction to Java Programming">
                    </div>
                    <div class="field" style="grid-column:1/-1">
                        <label for="author">Author</label>
                        <input type="text" id="author" name="author" required maxlength="120"
                               data-rule="name" data-label="Author" placeholder="Daniel Liang">
                    </div>
                </div>
                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">
                        <svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Add to catalogue
                    </button>
                </div>
            </form>
            <p class="tiny muted mb-0" style="margin-top:.9rem">
                All copies are available when a book is created; issuing reduces the available count.
            </p>
        </article>

        <%-- ------------------------------- SEARCH ------------------------------- --%>
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-search"/></svg> Search a book</h2>
                <span class="badge badge-info">Requirement 4</span>
            </div>
            <form method="get" action="${ctx}/admin/books" role="search">
                <div class="field mb-2">
                    <label for="q">Book ID, title or author</label>
                    <input type="search" id="q" name="q" placeholder="B003 or Algorithms&hellip;"
                           value="<c:out value='${keyword}'/>">
                </div>
                <div class="btn-row">
                    <button class="btn btn-primary" type="submit">
                        <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg> Search
                    </button>
                    <a class="btn btn-ghost" href="${ctx}/admin/books">Clear</a>
                </div>
            </form>
            <c:if test="${not empty keyword}">
                <p class="small muted" style="margin-top:1rem">
                    Showing <strong><c:out value="${fn:length(books)}"/></strong> result(s) for
                    &ldquo;<c:out value="${keyword}"/>&rdquo;.
                </p>
            </c:if>
        </article>
    </div>

    <%-- -------------------------------- TABLE -------------------------------- --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2>All books</h2>
                <span class="badge badge-muted"><c:out value="${fn:length(books)}"/> title(s)</span>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th>Book ID</th><th>Title</th><th>Author</th>
                        <th>Total copies</th><th>Available</th><th>Issued</th><th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="book" items="${books}">
                        <tr>
                            <td><span class="id-pill"><c:out value="${book.bookId}"/></span></td>
                            <td class="cell-strong"><c:out value="${book.title}"/></td>
                            <td class="muted"><c:out value="${book.author}"/></td>
                            <td class="num"><c:out value="${book.totalCopies}"/></td>
                            <td class="num copies-pill"><c:out value="${book.availableCopies}"/></td>
                            <td class="num"><c:out value="${book.issuedCopies}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${book.available}">
                                        <span class="badge badge-success">Available</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">All issued</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty books}">
                        <tr><td colspan="7">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg>
                                <p>No book matches this search. Try another ID, title or author.</p>
                            </div>
                        </td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
