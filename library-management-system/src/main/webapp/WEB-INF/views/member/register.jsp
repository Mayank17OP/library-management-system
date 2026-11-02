<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Member Registration"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card" style="max-width:520px">
        <span class="brand-mark"><svg class="icon" aria-hidden="true"><use href="#i-users"/></svg></span>
        <h1>Create a member account</h1>
        <p class="card-sub">
            Choose a unique member ID (letters and digits, e.g. <span class="id-pill">M105</span>).
            Your password is stored as a BCrypt hash, never as plain text.
        </p>

        <form method="post" action="${ctx}/member/register" data-validate>
            <div class="field">
                <label for="memberId">Member ID</label>
                <input type="text" id="memberId" name="memberId" required maxlength="12"
                       data-rule="id" data-label="Member ID" placeholder="M105"
                       value="<c:out value='${param.memberId}'/>">
                <span class="hint">2-12 letters or digits. Duplicate IDs are rejected.</span>
            </div>

            <div class="field">
                <label for="name">Full name</label>
                <input type="text" id="name" name="name" required maxlength="120"
                       data-rule="name" data-label="Name" placeholder="Your full name"
                       value="<c:out value='${param.name}'/>">
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required minlength="6"
                       data-rule="password" data-label="Password" placeholder="At least 6 characters">
            </div>

            <div class="field">
                <label for="confirmPassword">Confirm password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6"
                       data-rule="password" data-label="Password" placeholder="Repeat the password">
            </div>

            <button class="btn btn-primary btn-block gap-top" type="submit">
                <svg class="icon" aria-hidden="true"><use href="#i-plus"/></svg> Register
            </button>
        </form>

        <p class="auth-alt">
            Already registered? <a href="${ctx}/member/login">Log in</a>
        </p>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
