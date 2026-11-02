<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Something went wrong"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card center" style="max-width:560px">
        <span class="brand-mark" style="margin:0 auto 1rem;background:linear-gradient(140deg,#A31F26,#C0272D)">
            <svg class="icon" aria-hidden="true"><use href="#i-alert-triangle"/></svg>
        </span>
        <h1>500 &mdash; something went wrong</h1>
        <p class="card-sub">
            The request could not be completed. The technical details have been written to the server
            log; no stack trace is shown here on purpose.
        </p>
        <p class="small muted">
            The most common reason during a demo is that the MySQL database is not reachable.
            Check that the database is running and that <code>DB_URL</code>, <code>DB_USER</code> and
            <code>DB_PASSWORD</code> are correct.
        </p>
        <div class="btn-row" style="justify-content:center">
            <a class="btn btn-primary" href="${ctx}/home">
                <svg class="icon" aria-hidden="true"><use href="#i-home"/></svg> Back to home
            </a>
            <a class="btn btn-ghost" href="javascript:history.back()">Go back</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
