<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Member Login"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="brand-mark"><svg class="icon" aria-hidden="true"><use href="#i-user"/></svg></span>
        <h1>Member login</h1>
        <p class="card-sub">Sign in with your library member ID to borrow and return books.</p>

        <form method="post" action="${ctx}/login" data-validate>
            <input type="hidden" name="role" value="MEMBER">

            <div class="field">
                <label for="memberId">Member ID</label>
                <input type="text" id="memberId" name="memberId" required autocomplete="username"
                       data-rule="id" data-label="Member ID" placeholder="M001"
                       value="<c:out value='${param.memberId}'/>">
                <span class="hint">For example M001. The ID was given at registration.</span>
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="current-password"
                       data-rule="password" data-label="Password" placeholder="Your password">
            </div>

            <button class="btn btn-primary btn-block gap-top" type="submit">
                <svg class="icon" aria-hidden="true"><use href="#i-login"/></svg> Sign in
            </button>
        </form>

        <div class="demo-hint">
            <strong>Demo members:</strong> <code>M001</code> &hellip; <code>M004</code> &middot;
            password <code>member123</code><br>
            <span class="tiny">M004 is suspended in the demo data (book 101 days overdue).</span>
        </div>

        <p class="auth-alt">
            New here? <a href="${ctx}/member/register">Create a member account</a><br>
            Librarian? <a href="${ctx}/admin/login">Admin login</a>
        </p>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
