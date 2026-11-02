<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Page not found"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card center" style="max-width:520px">
        <span class="brand-mark" style="margin:0 auto 1rem">
            <svg class="icon" aria-hidden="true"><use href="#i-search"/></svg>
        </span>
        <h1>404 &mdash; page not found</h1>
        <p class="card-sub">
            The page you asked for does not exist in this application. It may have been typed
            incorrectly, or the link may be from an old version.
        </p>
        <div class="btn-row" style="justify-content:center">
            <a class="btn btn-primary" href="${ctx}/home">
                <svg class="icon" aria-hidden="true"><use href="#i-home"/></svg> Back to home
            </a>
            <a class="btn btn-ghost" href="${ctx}/about">About the project</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
