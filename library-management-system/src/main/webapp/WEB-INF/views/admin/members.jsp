<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Members"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Librarian panel</p>
        <h1>Members</h1>
        <p>Register new members and monitor their status. Suspension is automatic and cannot be set by hand.</p>
    </div>
</section>

<div class="container">
    <div class="split section">
        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-users"/></svg> Register a member</h2>
                <span class="badge badge-info">Requirement 5</span>
            </div>
            <p class="card-sub">The member ID must be unique. The password is hashed with BCrypt before it is stored.</p>

            <form method="post" action="${ctx}/admin/members" data-validate>
                <input type="hidden" name="action" value="add">
                <div class="form-grid">
                    <div class="field">
                        <label for="memberId">Member ID</label>
                        <input type="text" id="memberId" name="memberId" required maxlength="12"
                               placeholder="M005" data-rule="id" data-label="Member ID">
                    </div>
                    <div class="field">
                        <label for="name">Full name</label>
                        <input type="text" id="name" name="name" required maxlength="120"
                               placeholder="Student name" data-rule="name" data-label="Name">
                    </div>
                    <div class="field" style="grid-column:1/-1">
                        <label for="password">Initial password</label>
                        <input type="password" id="password" name="password" required minlength="6"
                               placeholder="At least 6 characters"
                               data-rule="password" data-label="Password">
                        <span class="hint">Give this password to the member; it is not shown again.</span>
                    </div>
                </div>
                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">
                        <svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Register member
                    </button>
                </div>
            </form>
        </article>

        <article class="card">
            <div class="card-head">
                <h2><svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg> Suspension rule</h2>
                <span class="badge badge-warning">Automatic</span>
            </div>
            <ul class="rule-list">
                <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>A member with an unreturned book that is <strong>more than 100 days</strong>
                        overdue becomes <strong>SUSPENDED</strong>.</span></li>
                <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>The check runs on login, on every dashboard load and after each issue or return.</span></li>
                <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>A suspended member cannot issue any book, but can still log in to see the fine.</span></li>
                <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>When the very late book is returned, the suspension is lifted automatically.</span></li>
            </ul>
            <p class="tiny muted mb-0" style="margin-top:.9rem">
                Use the <strong>Simulated Today</strong> control on the dashboard to push the date forward and
                show the suspension happening live.
            </p>
        </article>
    </div>

    <section class="section">
        <div class="card">
            <div class="card-head">
                <h2>All members</h2>
                <span class="badge badge-muted"><c:out value="${fn:length(members)}"/> member(s)</span>
            </div>
            <div class="table-wrap">
                <table class="data">
                    <thead>
                    <tr><th>Member ID</th><th>Name</th><th>Status</th><th>Can issue?</th></tr>
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
                            <td>
                                <c:choose>
                                    <c:when test="${member.active}">
                                        <span class="badge badge-success">Yes</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">No &ndash; suspended</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty members}">
                        <tr><td colspan="4"><div class="empty"><p>No members registered yet.</p></div></td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
