<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reports"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Librarian panel</p>
        <h1>Library reports</h1>
        <p>Snapshot for <strong><c:out value="${today}"/></strong><c:if
                test="${not empty simulatedDate}"> (simulated date)</c:if></p>
    </div>
</section>

<div class="container">

    <%-- ===================== REQUIRED STAT CARDS (Requirement 13) ===================== --%>
    <section class="section">
        <div class="stat-grid">
            <article class="stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-book"/></svg></span>
                <div class="stat-value"><c:out value="${stats.totalTitles}"/></div>
                <div class="stat-label">Total books</div>
                <div class="tiny muted"><c:out value="${stats.totalCopies}"/> physical copies</div>
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
                <div class="tiny muted"><c:out value="${stats.overdueLoans}"/> overdue</div>
            </article>
            <article class="stat alert-stat">
                <span class="stat-icon"><svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg></span>
                <div class="stat-value"><c:out value="${stats.suspendedMembers}"/></div>
                <div class="stat-label">Suspended members</div>
                <div class="tiny muted">more than 100 days overdue</div>
            </article>
        </div>
    </section>

    <div class="split section">
        <%-- ------------------------------- CHART ------------------------------- --%>
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-chart"/></svg> Availability chart</h2>
                <span class="badge badge-info">Live data</span>
            </div>

            <div class="chart">
                <div class="chart-row">
                    <div class="chart-label">
                        <span>Copies available on the shelf</span>
                        <b><c:out value="${stats.availableCopies}"/> / <c:out value="${stats.totalCopies}"/></b>
                    </div>
                    <div class="chart-track">
                        <div class="chart-fill fill-green" style="width:${stats.availablePercent}%"></div>
                    </div>
                </div>

                <div class="chart-row">
                    <div class="chart-label">
                        <span>Copies issued to members</span>
                        <b><c:out value="${stats.issuedCopies}"/> / <c:out value="${stats.totalCopies}"/></b>
                    </div>
                    <div class="chart-track">
                        <div class="chart-fill" style="width:${stats.issuedPercent}%"></div>
                    </div>
                </div>

                <div class="chart-row">
                    <div class="chart-label">
                        <span>Active loans that are overdue</span>
                        <b><c:out value="${stats.overdueLoans}"/> / <c:out value="${stats.activeLoans}"/></b>
                    </div>
                    <div class="chart-track">
                        <div class="chart-fill fill-amber" style="width:${stats.overduePercent}%"></div>
                    </div>
                </div>

                <div class="chart-row">
                    <div class="chart-label">
                        <span>Members suspended</span>
                        <b><c:out value="${stats.suspendedMembers}"/> / <c:out value="${stats.totalMembers}"/></b>
                    </div>
                    <div class="chart-track">
                        <div class="chart-fill fill-red" style="width:${stats.suspendedPercent}%"></div>
                    </div>
                </div>
            </div>

            <hr style="border:0;border-top:1px dashed #DCE6F4;margin:1.3rem 0">

            <dl class="kv">
                <dt>Fines collected</dt><dd class="fine">Rs <c:out value="${stats.finesCollected}"/></dd>
                <dt>Fines pending</dt><dd class="fine">Rs <c:out value="${stats.finesOutstanding}"/>
                    <span class="tiny muted">(if every overdue book came back today)</span></dd>
                <dt>Calculation</dt><dd class="small muted">Rs 2 &times; days after the due date</dd>
            </dl>
        </article>

        <%-- --------------------------- PRINTABLE SUMMARY --------------------------- --%>
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-list"/></svg> Monthly summary</h2>
                <span class="badge badge-muted">For the report file</span>
            </div>
            <div class="table-wrap">
                <table class="data compact">
                    <tbody>
                    <tr><td class="cell-strong">Book titles</td><td class="num"><c:out value="${stats.totalTitles}"/></td></tr>
                    <tr><td class="cell-strong">Total copies</td><td class="num"><c:out value="${stats.totalCopies}"/></td></tr>
                    <tr><td class="cell-strong">Copies available</td><td class="num"><c:out value="${stats.availableCopies}"/></td></tr>
                    <tr><td class="cell-strong">Copies issued</td><td class="num"><c:out value="${stats.issuedCopies}"/></td></tr>
                    <tr><td class="cell-strong">Registered members</td><td class="num"><c:out value="${stats.totalMembers}"/></td></tr>
                    <tr><td class="cell-strong">Active loans</td><td class="num"><c:out value="${stats.activeLoans}"/></td></tr>
                    <tr><td class="cell-strong">Overdue loans</td><td class="num"><c:out value="${stats.overdueLoans}"/></td></tr>
                    <tr><td class="cell-strong">Suspended members</td><td class="num"><c:out value="${stats.suspendedMembers}"/></td></tr>
                    <tr><td class="cell-strong">Fines collected (Rs)</td><td class="num"><c:out value="${stats.finesCollected}"/></td></tr>
                    </tbody>
                </table>
            </div>
            <button class="btn btn-ghost btn-block gap-top" type="button" onclick="window.print()">
                <svg class="icon" aria-hidden="true"><use href="#i-printer"/></svg> Print this report
            </button>
        </article>
    </div>

    <%-- --------------------------- OVERDUE LOANS LIST --------------------------- --%>
    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-clock"/></svg> Overdue books</h2>
                <span class="badge badge-danger"><c:out value="${stats.overdueLoans}"/> overdue</span>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr><th>Loan #</th><th>Member</th><th>Book</th><th>Due date</th>
                        <th>Days late</th><th>Fine so far</th></tr>
                    </thead>
                    <tbody>
                    <c:set var="overdueCount" value="0"/>
                    <c:forEach var="loan" items="${loans}">
                        <c:if test="${not loan.returned and loan.daysOverdue > 0}">
                            <c:set var="overdueCount" value="${overdueCount + 1}"/>
                            <tr>
                                <td class="num">#<c:out value="${loan.loanId}"/></td>
                                <td><span class="id-pill"><c:out value="${loan.memberId}"/></span>
                                    <div class="tiny muted"><c:out value="${loan.memberName}"/></div></td>
                                <td class="cell-strong"><c:out value="${loan.bookTitle}"/></td>
                                <td class="num"><c:out value="${loan.dueDate}"/></td>
                                <td class="num"><span class="badge badge-warning">
                                    <c:out value="${loan.daysOverdue}"/> days</span></td>
                                <td class="num fine fine-due">Rs <c:out value="${loan.currentFine}"/></td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    <c:if test="${overdueCount == 0}">
                        <tr><td colspan="6">
                            <div class="empty">
                                <svg class="icon" aria-hidden="true"><use href="#i-check-circle"/></svg>
                                <p>No overdue books. Every loan is within its due date.</p>
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
