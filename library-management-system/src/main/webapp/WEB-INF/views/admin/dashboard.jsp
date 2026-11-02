<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Librarian panel</p>
        <h1>Admin dashboard</h1>
        <p>Today in the system: <strong><c:out value="${today}"/></strong>
            <c:if test="${not empty simulatedDate}"> (simulated)</c:if>
            &middot; everything below is calculated with this date.</p>
    </div>
</section>

<div class="container">

    <%-- ======================= STAT CARDS (Requirement 13) ======================= --%>
    <section class="section">
        <div class="stat-grid">
            <article class="stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-book"/></svg></span>
                <div class="stat-value"><c:out value="${stats.totalTitles}"/></div>
                <div class="stat-label">Total books (titles)</div>
                <div class="tiny muted"><c:out value="${stats.totalCopies}"/> copies in total</div>
            </article>

            <article class="stat good-stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-users"/></svg></span>
                <div class="stat-value"><c:out value="${stats.totalMembers}"/></div>
                <div class="stat-label">Total members</div>
                <div class="tiny muted"><c:out value="${stats.activeMembers}"/> active</div>
            </article>

            <article class="stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg></span>
                <div class="stat-value"><c:out value="${stats.activeLoans}"/></div>
                <div class="stat-label">Active loans</div>
                <div class="tiny muted"><c:out value="${stats.availableCopies}"/> copies available</div>
            </article>

            <article class="stat alert-stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg></span>
                <div class="stat-value"><c:out value="${stats.suspendedMembers}"/></div>
                <div class="stat-label">Suspended members</div>
                <div class="tiny muted">book more than 100 days late</div>
            </article>

            <article class="stat warn-stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-clock"/></svg></span>
                <div class="stat-value"><c:out value="${stats.overdueLoans}"/></div>
                <div class="stat-label">Overdue loans</div>
                <div class="tiny muted">Rs <c:out value="${stats.finesOutstanding}"/> pending</div>
            </article>

            <article class="stat good-stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-check-circle"/></svg></span>
                <div class="stat-value">Rs <c:out value="${stats.finesCollected}"/></div>
                <div class="stat-label">Fines collected</div>
                <div class="tiny muted">from returned books</div>
            </article>
        </div>
    </section>

    <%-- ============ SIMULATED DATE + ADD BOOK (side by side) ============ --%>
    <section class="section split">

        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-calendar"/></svg> Simulated today (demo)</h2>
                <c:choose>
                    <c:when test="${not empty simulatedDate}">
                        <span class="badge badge-warning">Active: <c:out value="${simulatedDate}"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-muted">Real date</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <p class="card-sub">
                Override the system date to demonstrate overdue fines, renewal blocking and automatic
                suspension instantly. The value is stored in the database, so it survives a restart.
            </p>

            <form method="post" action="${ctx}/admin/settings" class="form-grid">
                <input type="hidden" name="action" value="set-date">
                <div class="field">
                    <label for="date">Simulate this date</label>
                    <input type="date" id="date" name="date" required
                           value="<c:out value='${simulatedDate}'/>">
                </div>
                <div class="field">
                    <label>&nbsp;</label>
                    <button class="btn btn-primary" type="submit">
                        <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg> Apply date
                    </button>
                </div>
            </form>

            <div class="btn-row gap-top">
                <form method="post" action="${ctx}/admin/settings">
                    <input type="hidden" name="action" value="shift">
                    <input type="hidden" name="days" value="5">
                    <button class="btn btn-ghost btn-sm" type="submit">Quick +5 days</button>
                </form>
                <form method="post" action="${ctx}/admin/settings">
                    <input type="hidden" name="action" value="shift">
                    <input type="hidden" name="days" value="15">
                    <button class="btn btn-ghost btn-sm" type="submit">+15 days</button>
                </form>
                <form method="post" action="${ctx}/admin/settings">
                    <input type="hidden" name="action" value="shift">
                    <input type="hidden" name="days" value="105">
                    <button class="btn btn-ghost btn-sm" type="submit">+105 days (suspension)</button>
                </form>
                <form method="post" action="${ctx}/admin/settings">
                    <input type="hidden" name="action" value="clear-date">
                    <button class="btn btn-outline btn-sm" type="submit">
                        <svg class="icon" aria-hidden="true"><use href="#i-reset"/></svg> Use real date
                    </button>
                </form>
            </div>

            <hr style="border:0;border-top:1px dashed #DCE6F4;margin:1.2rem 0">

            <form method="post" action="${ctx}/admin/settings" data-confirm="This deletes all current loans, books, members and settings and reloads the demo set. Continue?">
                <input type="hidden" name="action" value="reset-demo">
                <button class="btn btn-danger btn-block" type="submit">
                    <svg class="icon" aria-hidden="true"><use href="#i-reset"/></svg> Reset demo data
                </button>
            </form>
            <p class="tiny muted mb-0" style="margin-top:.6rem">
                Restores 12 books, 4 members and 5 loans (1 on time, 1 five days overdue, 1 more than
                100 days overdue).
            </p>
        </article>

        <%-- ---------------------------- ADD BOOK ---------------------------- --%>
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Add a book</h2>
                <span class="badge badge-info">Requirement 2</span>
            </div>
            <p class="card-sub">The number of copies becomes both the total and the available count.</p>

            <form method="post" action="${ctx}/admin/books" data-validate>
                <input type="hidden" name="action" value="add">
                <div class="field mb-2">
                    <label for="bookId">Book ID</label>
                    <input type="text" id="bookId" name="bookId" required maxlength="12" placeholder="B013"
                           data-rule="id" data-label="Book ID">
                </div>
                <div class="field mb-2">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" required maxlength="120"
                           data-rule="name" data-label="Title" placeholder="Introduction to Java Programming">
                </div>
                <div class="field mb-2">
                    <label for="author">Author</label>
                    <input type="text" id="author" name="author" required maxlength="120"
                           data-rule="name" data-label="Author" placeholder="Daniel Liang">
                </div>
                <div class="field mb-2">
                    <label for="totalCopies">Total copies</label>
                    <input type="number" id="totalCopies" name="totalCopies" required min="1" max="999"
                           value="1" data-rule="positive" data-label="Copies">
                </div>
                <button class="btn btn-primary btn-block" type="submit">
                    <svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Add to catalogue
                </button>
            </form>
        </article>
    </section>

    <%-- ============================== BOOKS ============================== --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-book-open"/></svg> Catalogue</h2>
                <form method="get" action="${ctx}/admin/books" class="search-bar" role="search">
                    <span class="search-icon">
                        <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg>
                        <input type="search" name="q" placeholder="Search ID, title or author&hellip;"
                               aria-label="Search the catalogue">
                    </span>
                    <button class="btn btn-ghost btn-sm" type="submit">Search</button>
                </form>
            </div>

            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th>Book ID</th><th>Title</th><th>Author</th>
                        <th>Total</th><th>Available</th><th>Status</th>
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
                        <tr><td colspan="6">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-book"/></svg>
                                <p>No books yet. Add the first title with the form above.</p>
                            </div>
                        </td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <%-- ========================= ACTIVE LOANS + ISSUE ========================= --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg> Books currently issued</h2>
                <a class="btn btn-ghost btn-sm" href="${ctx}/admin/loans">Full loan register</a>
            </div>

            <%-- Issue a book: librarian enters the member ID and the book ID --%>
            <form method="post" action="${ctx}/admin/circulation" class="inline-form mb-3"
                  data-confirm="Issue this book to the member?">
                <input type="hidden" name="action" value="issue">
                <input type="hidden" name="back" value="dashboard">
                <div class="field">
                    <label for="issueMember">Member ID</label>
                    <input type="text" id="issueMember" name="memberId" required placeholder="M001">
                </div>
                <div class="field">
                    <label for="issueBook">Book ID</label>
                    <input type="text" id="issueBook" name="bookId" required placeholder="B002">
                </div>
                <button class="btn btn-primary" type="submit">
                    <svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg> Issue book
                </button>
            </form>

            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th>#</th><th>Member</th><th>Book</th><th>Issued</th><th>Due</th>
                        <th>Status</th><th>Fine</th><th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="loan" items="${loans}">
                        <tr>
                            <td class="num">#<c:out value="${loan.loanId}"/></td>
                            <td>
                                <span class="id-pill"><c:out value="${loan.memberId}"/></span>
                                <div class="tiny muted"><c:out value="${loan.memberName}"/></div>
                            </td>
                            <td>
                                <span class="cell-strong"><c:out value="${loan.bookTitle}"/></span>
                                <div class="tiny muted"><c:out value="${loan.bookId}"/></div>
                            </td>
                            <td class="num"><c:out value="${loan.issueDate}"/></td>
                            <td class="num"><c:out value="${loan.dueDate}"/></td>
                            <td><span class="badge ${loan.statusCssClass}"><c:out value="${loan.statusText}"/></span></td>
                            <td class="num fine">
                                <c:choose>
                                    <c:when test="${loan.currentFine > 0}">
                                        <span class="fine-due">Rs <c:out value="${loan.currentFine}"/></span>
                                    </c:when>
                                    <c:otherwise><span class="fine-zero">Rs 0</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="cell-actions">
                                <form method="post" action="${ctx}/admin/circulation">
                                    <input type="hidden" name="action" value="return">
                                    <input type="hidden" name="loanId" value="${loan.loanId}">
                                    <input type="hidden" name="back" value="dashboard">
                                    <button class="btn btn-sm btn-success" type="submit">Return</button>
                                </form>
                                <form method="post" action="${ctx}/admin/circulation">
                                    <input type="hidden" name="action" value="renew">
                                    <input type="hidden" name="loanId" value="${loan.loanId}">
                                    <input type="hidden" name="back" value="dashboard">
                                    <button class="btn btn-sm btn-outline" type="submit">Renew</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty loans}">
                        <tr><td colspan="8">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-check-circle"/></svg>
                                <p>No books are issued at the moment.</p>
                            </div>
                        </td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <%-- ============================== MEMBERS ============================== --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-users"/></svg> Members</h2>
                <a class="btn btn-ghost btn-sm" href="${ctx}/admin/members">Manage members</a>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr><th>Member ID</th><th>Name</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="member" items="${members}">
                        <tr>
                            <td><span class="id-pill"><c:out value="${member.memberId}"/></span></td>
                            <td class="cell-strong"><c:out value="${member.name}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${member.suspended}">
                                        <span class="badge badge-danger">Suspended</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-success">Active</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty members}">
                        <tr><td colspan="3"><div class="empty"><p>No members registered yet.</p></div></td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
