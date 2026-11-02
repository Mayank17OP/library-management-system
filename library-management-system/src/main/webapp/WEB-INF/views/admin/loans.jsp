<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Loan Register"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Librarian panel</p>
        <h1>Loan register</h1>
        <p>Every issue, return and renewal. Today in the system is <strong><c:out value="${today}"/></strong>.</p>
    </div>
</section>

<div class="container">
    <%-- ------------------------------- ISSUE ------------------------------- --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg> Issue a book</h2>
                <span class="badge badge-info">Requirement 7</span>
            </div>
            <form method="post" action="${ctx}/admin/circulation" class="inline-form"
                  data-confirm="Issue this book to this member?">
                <input type="hidden" name="action" value="issue">
                <input type="hidden" name="back" value="loans">
                <div class="field">
                    <label for="memberId">Member ID</label>
                    <input type="text" id="memberId" name="memberId" required placeholder="M001">
                </div>
                <div class="field">
                    <label for="bookId">Book ID</label>
                    <input type="text" id="bookId" name="bookId" required placeholder="B004">
                </div>
                <button class="btn btn-primary" type="submit">
                    <svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg> Issue
                </button>
            </form>
            <p class="tiny muted mb-0" style="margin-top:.8rem">
                The issue is refused when the member is suspended, no copy is available, or the member
                already has this title.
            </p>
        </div>
    </section>

    <%-- ------------------------------ REGISTER ------------------------------ --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2>All loans</h2>
                <span class="badge badge-muted"><c:out value="${fn:length(loans)}"/> record(s)</span>
            </div>

            <div class="field mb-2" style="max-width:340px">
                <label for="filterLoans">Quick filter</label>
                <input type="search" id="filterLoans" placeholder="Filter by member, book or status&hellip;"
                       data-table-filter="#loansTable">
            </div>

            <div class="table-wrap">
                <table class="data" id="loansTable">
                    <thead>
                    <tr>
                        <th>Loan #</th><th>Member</th><th>Book</th><th>Issue date</th><th>Due date</th>
                        <th>Return date</th><th>Renewed</th><th>Status</th><th>Fine</th><th>Actions</th>
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
                            <td class="num"><c:out value="${empty loan.returnDate ? '-' : loan.returnDate}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${loan.renewed}">
                                        <span class="badge badge-warning">Yes</span>
                                    </c:when>
                                    <c:otherwise><span class="badge badge-muted">No</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><span class="badge ${loan.statusCssClass}"><c:out value="${loan.statusText}"/></span></td>
                            <td class="num fine">
                                <c:choose>
                                    <c:when test="${loan.returned}">
                                        <span class="${loan.fine > 0 ? 'fine-due' : 'fine-zero'}">
                                            Rs <c:out value="${loan.fine}"/></span>
                                    </c:when>
                                    <c:when test="${loan.currentFine > 0}">
                                        <span class="fine-due">Rs <c:out value="${loan.currentFine}"/>
                                            <span class="tiny muted">(running)</span></span>
                                    </c:when>
                                    <c:otherwise><span class="fine-zero">Rs 0</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="cell-actions">
                                <c:if test="${not loan.returned}">
                                    <form method="post" action="${ctx}/admin/circulation">
                                        <input type="hidden" name="action" value="return">
                                        <input type="hidden" name="loanId" value="${loan.loanId}">
                                        <input type="hidden" name="back" value="loans">
                                        <button class="btn btn-sm btn-success" type="submit">Return</button>
                                    </form>
                                    <form method="post" action="${ctx}/admin/circulation">
                                        <input type="hidden" name="action" value="renew">
                                        <input type="hidden" name="loanId" value="${loan.loanId}">
                                        <input type="hidden" name="back" value="loans">
                                        <button class="btn btn-sm btn-outline" type="submit">Renew</button>
                                    </form>
                                </c:if>
                                <c:if test="${loan.returned}">
                                    <span class="tiny muted">closed</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty loans}">
                        <tr><td colspan="10">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-list"/></svg>
                                <p>The register is empty. Issue a book above to create the first loan.</p>
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
