<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Librarian Login"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="brand-mark"><svg class="icon" aria-hidden="true"><use href="#i-lock"/></svg></span>
        <h1>Librarian login</h1>
        <p class="card-sub">Administrator access to the catalogue, members, loans and reports.</p>

        <form method="post" action="${ctx}/login" data-validate>
            <input type="hidden" name="role" value="ADMIN">

            <div class="field">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required autocomplete="username"
                       data-rule="name" data-label="Username" placeholder="admin"
                       value="<c:out value='${param.username}'/>">
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="current-password"
                       data-rule="password" data-label="Password" placeholder="Your password">
            </div>

            <button class="btn btn-primary btn-block gap-top" type="submit">
                <svg class="icon" aria-hidden="true"><use href="#i-login"/></svg> Sign in as librarian
            </button>
        </form>

        <%-- Demo hint required by the project statement --%>
        <div class="demo-hint">
            <strong>Demo credentials:</strong> username <code>admin</code> &middot; password <code>admin123</code>
        </div>

        <p class="auth-alt">
            Are you a student? <a href="${ctx}/member/login">Go to the member login</a>
        </p>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
