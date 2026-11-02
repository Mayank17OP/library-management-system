<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My Dashboard"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Member area</p>
        <h1>Hello, <c:out value="${member.name}"/></h1>
        <p>Member ID <strong><c:out value="${member.memberId}"/></strong>
            &middot; Today in the system: <strong><c:out value="${today}"/></strong>
            &middot; <c:out value="${openLoans}"/> book(s) with you.</p>
    </div>
</section>

<div class="container">

    <%-- Suspension warning (Requirement 12) --%>
    <c:if test="${member.suspended}">
        <div class="alert alert-error mb-3" role="alert">
            <svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg>
            <span><strong>Your membership is suspended.</strong> A book is more than 100 days overdue,
                so issuing new books is blocked. Please return the book at the library counter &mdash;
                the suspension is lifted automatically once nothing is beyond the limit.</span>
        </div>
    </c:if>

    <%-- ============================== MY SUMMARY ============================== --%>
    <section class="section">
        <div class="stat-grid">
            <article class="stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-book-open"/></svg></span>
                <div class="stat-value"><c:out value="${openLoans}"/></div>
                <div class="stat-label">Books with me</div>
            </article>
            <article class="stat ${overdueLoans > 0 ? 'alert-stat' : 'good-stat'}">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-clock"/></svg></span>
                <div class="stat-value"><c:out value="${overdueLoans}"/></div>
                <div class="stat-label">Overdue</div>
            </article>
            <article class="stat ${member.suspended ? 'alert-stat' : 'good-stat'}">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg></span>
                <div class="stat-value" style="font-size:1.25rem">
                    <c:out value="${member.suspended ? 'Suspended' : 'Active'}"/>
                </div>
                <div class="stat-label">Membership status</div>
            </article>
            <article class="stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-calendar"/></svg></span>
                <div class="stat-value" style="font-size:1.15rem">14 days</div>
                <div class="stat-label">Loan period &middot; Rs 2 fine per late day</div>
            </article>
        </div>
    </section>

    <%-- ============================== MY LOANS ============================== --%>
    <section class="section" id="my-loans">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-list"/></svg> My loans</h2>
                <span class="badge badge-muted"><c:out value="${fn:length(myLoans)}"/> record(s)</span>
            </div>

            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr>
                        <th>Book</th><th>Issued</th><th>Due</th><th>Returned</th>
                        <th>Status</th><th>Fine</th><th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="loan" items="${myLoans}">
                        <tr>
                            <td>
                                <span class="cell-strong"><c:out value="${loan.bookTitle}"/></span>
                                <div class="tiny muted"><c:out value="${loan.bookId}"/>
                                    <c:if test="${loan.renewed}"> &middot; renewed once</c:if></div>
                            </td>
                            <td class="num"><c:out value="${loan.issueDate}"/></td>
                            <td class="num"><c:out value="${loan.dueDate}"/></td>
                            <td class="num"><c:out value="${empty loan.returnDate ? '-' : loan.returnDate}"/></td>
                            <td><span class="badge ${loan.statusCssClass}"><c:out value="${loan.statusText}"/></span></td>
                            <td class="num fine">
                                <c:choose>
                                    <c:when test="${loan.returned}">
                                        <span class="${loan.fine > 0 ? 'fine-due' : 'fine-zero'}">
                                            Rs <c:out value="${loan.fine}"/></span>
                                    </c:when>
                                    <c:when test="${loan.currentFine > 0}">
                                        <span class="fine-due">Rs <c:out value="${loan.currentFine}"/>
                                            <span class="tiny muted">(so far)</span></span>
                                    </c:when>
                                    <c:otherwise><span class="fine-zero">Rs 0</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="cell-actions">
                                <c:choose>
                                    <c:when test="${loan.returned}">
                                        <span class="tiny muted">closed</span>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="post" action="${ctx}/member/circulation">
                                            <input type="hidden" name="action" value="return">
                                            <input type="hidden" name="loanId" value="${loan.loanId}">
                                            <button class="btn btn-sm btn-success" type="submit">Return</button>
                                        </form>
                                        <c:choose>
                                            <c:when test="${loan.daysOverdue == 0 and not loan.renewed}">
                                                <form method="post" action="${ctx}/member/circulation">
                                                    <input type="hidden" name="action" value="renew">
                                                    <input type="hidden" name="loanId" value="${loan.loanId}">
                                                    <button class="btn btn-sm btn-outline" type="submit">Renew</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-sm btn-outline is-disabled" type="button"
                                                        disabled
                                                        title="${loan.renewed ? 'Already renewed once' : 'Overdue books cannot be renewed'}">
                                                    Renew
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty myLoans}">
                        <tr><td colspan="7">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-book"/></svg>
                                <p>You have not borrowed any book yet. Pick one from the catalogue below.</p>
                            </div>
                        </td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <p class="tiny muted mb-0" style="margin-top:.8rem">
                Renewal gives 7 extra days and is allowed only once. It is blocked when the book is
                already overdue &mdash; return the book and pay the fine instead.
            </p>
        </div>
    </section>

    <%-- ============================== BROWSE BOOKS ============================== --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-book-open"/></svg> Browse the catalogue</h2>
                <form method="get" action="${ctx}/member/dashboard" class="search-bar" role="search">
                    <span class="search-icon">
                        <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg>
                        <input type="search" name="q" placeholder="Search ID, title or author&hellip;"
                               aria-label="Search the catalogue"
                               value="<c:out value='${keyword}'/>">
                    </span>
                    <button class="btn btn-ghost btn-sm" type="submit">Search</button>
                </form>
            </div>

            <c:if test="${not empty keyword}">
                <p class="small muted">
                    Showing <strong><c:out value="${fn:length(books)}"/></strong> result(s) for
                    &ldquo;<c:out value="${keyword}"/>&rdquo; &middot;
                    <a href="${ctx}/member/dashboard">show all</a>
                </p>
            </c:if>

            <div class="grid grid-auto">
                <c:forEach var="book" items="${books}">
                    <article class="book-card ${book.available ? '' : 'is-out'}">
                        <h3><c:out value="${book.title}"/></h3>
                        <p class="book-author"><c:out value="${book.author}"/></p>

                        <div class="book-meta">
                            <span>
                                <span class="id-pill"><c:out value="${book.bookId}"/></span>
                                <span class="copies-pill"><c:out value="${book.availableCopies}"/>/<c:out value="${book.totalCopies}"/></span>
                                available
                            </span>

                            <c:choose>
                                <c:when test="${not book.available}">
                                    <span class="badge badge-danger">All issued</span>
                                </c:when>
                                <c:when test="${member.suspended}">
                                    <button class="btn btn-sm btn-outline is-disabled" type="button" disabled
                                            title="Your membership is suspended">Issue</button>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${ctx}/member/circulation">
                                        <input type="hidden" name="action" value="issue">
                                        <input type="hidden" name="bookId" value="${book.bookId}">
                                        <button class="btn btn-sm btn-primary" type="submit">
                                            <svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg>
                                            Issue
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${empty books}">
                <div class="empty">
                    <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg>
                    <p>No book matches this search. Try a different ID, title or author.</p>
                </div>
            </c:if>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
