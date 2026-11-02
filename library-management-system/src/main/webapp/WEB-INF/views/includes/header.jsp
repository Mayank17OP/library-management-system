<%--
    Shared page head + navbar + flash messages.
    Every page starts with:
        <c:set var="pageTitle" value="..."/>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="role" value="${sessionScope.role}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'Library Management System' : pageTitle}"/> | DTC Library</title>
    <meta name="description" content="Library Management System - book issue, return, renewal, fines and reports. Software Engineering CIC-309, Delhi Technical Campus.">
    <meta name="theme-color" content="#0B3A7A">
    <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&amp;family=Inter:wght@400;500;600;700&amp;display=swap">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<a class="skip-link" href="#main">Skip to main content</a>

<%@ include file="icons.jsp" %>

<header class="navbar">
    <div class="container nav-inner">
        <a class="brand" href="${ctx}/home" aria-label="Library Management System home">
            <span class="brand-mark"><svg class="icon" aria-hidden="true"><use href="#i-book"/></svg></span>
            <span class="brand-text">
                <strong>DTC Library</strong>
                <small>Management System</small>
            </span>
        </a>

        <button class="nav-toggle" id="navToggle" type="button"
                aria-expanded="false" aria-controls="navMenu" aria-label="Toggle navigation menu">
            <svg class="icon" aria-hidden="true"><use href="#i-menu"/></svg>
        </button>

        <nav class="nav-links" id="navMenu" aria-label="Main navigation">
            <c:choose>
                <c:when test="${role == 'ADMIN'}">
                    <a href="${ctx}/admin/dashboard">Dashboard</a>
                    <a href="${ctx}/admin/books">Books</a>
                    <a href="${ctx}/admin/members">Members</a>
                    <a href="${ctx}/admin/loans">Loans</a>
                    <a href="${ctx}/admin/reports">Reports</a>
                    <a href="${ctx}/about">About</a>
                    <a href="${ctx}/logout" class="is-cta">
                        <svg class="icon" aria-hidden="true"><use href="#i-logout"/></svg> Logout
                    </a>
                </c:when>
                <c:when test="${role == 'MEMBER'}">
                    <a href="${ctx}/member/dashboard">My Dashboard</a>
                    <a href="${ctx}/home">Home</a>
                    <a href="${ctx}/about">About</a>
                    <a href="${ctx}/logout" class="is-cta">
                        <svg class="icon" aria-hidden="true"><use href="#i-logout"/></svg> Logout
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/home">Home</a>
                    <a href="${ctx}/about">About</a>
                    <a href="${ctx}/member/login">Member Login</a>
                    <a href="${ctx}/admin/login" class="is-cta">
                        <svg class="icon" aria-hidden="true"><use href="#i-lock"/></svg> Librarian Login
                    </a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<%-- Visible banner whenever the demo "Simulated Today" date is active --%>
<c:if test="${not empty simulatedDate}">
    <div class="sim-banner" role="status">
        <svg class="icon" aria-hidden="true"><use href="#i-alert-triangle"/></svg>
        <span>Simulated date active: <strong><c:out value="${simulatedDate}"/></strong>
            (real date: <span id="realToday">today</span>). Fines, renewals and suspension are calculated
            using the simulated date.</span>
        <c:if test="${role == 'ADMIN'}">
            <form method="post" action="${ctx}/admin/settings" style="display:inline">
                <input type="hidden" name="action" value="clear-date">
                <button class="btn btn-sm btn-light" type="submit">Use real date</button>
            </form>
        </c:if>
    </div>
</c:if>

<%-- Success / error messages coming from a redirect (Post/Redirect/Get) --%>
<c:if test="${not empty sessionScope.flashSuccess or not empty sessionScope.flashError or not empty sessionScope.flashInfo}">
    <div class="flash-stack">
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success" role="status">
                <svg class="icon" aria-hidden="true"><use href="#i-check-circle"/></svg>
                <span>${fn:escapeXml(sessionScope.flashSuccess)}</span>
            </div>
            <c:remove var="flashSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-error" role="alert">
                <svg class="icon" aria-hidden="true"><use href="#i-alert-circle"/></svg>
                <span>${fn:escapeXml(sessionScope.flashError)}</span>
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashInfo}">
            <div class="alert alert-info" role="status">
                <svg class="icon" aria-hidden="true"><use href="#i-info"/></svg>
                <span>${fn:escapeXml(sessionScope.flashInfo)}</span>
            </div>
            <c:remove var="flashInfo" scope="session"/>
        </c:if>
    </div>
</c:if>

<main id="main">
