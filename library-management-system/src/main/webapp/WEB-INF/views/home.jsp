<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Home"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<%-- ============================== HERO ============================== --%>
<section class="hero">
    <div class="container hero-grid">
        <div>
            <h1>Every book, every loan,<br><span class="accent">under control.</span></h1>
            <p class="lead">
                A digital replacement for the paper library register: issue and return books,
                renew loans, calculate fines automatically, suspend defaulters and generate
                reports &mdash; in one clean web application.
            </p>
            <div class="hero-actions">
                <a class="btn btn-light" href="${ctx}/member/login">
                    <svg class="icon" aria-hidden="true"><use href="#i-user"/></svg> Member Login
                </a>
                <a class="btn btn-primary" href="${ctx}/admin/login">
                    <svg class="icon" aria-hidden="true"><use href="#i-lock"/></svg> Librarian Login
                </a>
                <a class="btn btn-outline" href="${ctx}/about" style="color:#fff;border-color:rgba(255,255,255,.5)">
                    About the project
                </a>
            </div>

            <div class="hero-facts">
                <div class="hero-fact">
                    <strong><c:out value="${empty stats ? 0 : stats.totalTitles}"/></strong>
                    <span>Titles catalogued</span>
                </div>
                <div class="hero-fact">
                    <strong><c:out value="${empty stats ? 0 : stats.totalMembers}"/></strong>
                    <span>Members</span>
                </div>
                <div class="hero-fact">
                    <strong><c:out value="${empty stats ? 0 : stats.activeLoans}"/></strong>
                    <span>Books issued</span>
                </div>
                <div class="hero-fact">
                    <strong>Rs 2</strong>
                    <span>Fine per late day</span>
                </div>
            </div>
        </div>

        <aside class="hero-panel" aria-label="Library rules">
            <h2><svg class="icon" aria-hidden="true"><use href="#i-shield"/></svg> Library rules at a glance</h2>
            <div class="rule-row"><span>Loan period</span><b>14 days</b></div>
            <div class="rule-row"><span>Renewal</span><b>once, +7 days</b></div>
            <div class="rule-row"><span>Fine for a late return</span><b>Rs 2 / day</b></div>
            <div class="rule-row"><span>Suspension</span><b>over 100 days late</b></div>
            <div class="rule-row"><span>Issue condition</span><b>copy available &amp; member active</b></div>
        </aside>
    </div>
</section>

<%-- ============================ FEATURES ============================ --%>
<section class="container section" style="margin-top:2.6rem">
    <div class="section-head">
        <div>
            <span class="eyebrow">What the system does</span>
            <h2>Everything the register used to do &mdash; automatically</h2>
        </div>
    </div>

    <div class="grid grid-3">
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-book-open"/></svg></span>
            <h3>Catalogue management</h3>
            <p>Add books with total and available copies, browse the catalogue and search by book ID,
               title or author in one box.</p>
        </article>
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-arrow-right"/></svg></span>
            <h3>Issue &amp; return</h3>
            <p>A book can only be issued when a copy is free and the member is not suspended.
               Availability is updated in the same step, so the count can never drift.</p>
        </article>
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-refresh"/></svg></span>
            <h3>Renewal with rules</h3>
            <p>One renewal per loan, adding 7 days &mdash; and only when the book is not already
               overdue. Otherwise the member sees the exact reason.</p>
        </article>
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-clock"/></svg></span>
            <h3>Automatic fines</h3>
            <p>Rs 2 for every day after the due date, calculated the moment the book is returned
               and shown live on every open loan.</p>
        </article>
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-shield"/></svg></span>
            <h3>Defaulter control</h3>
            <p>If a book stays out for more than 100 days the member is suspended automatically
               at login and on every dashboard load, and cannot issue again.</p>
        </article>
        <article class="card feature">
            <span class="icon-chip"><svg class="icon icon-lg" aria-hidden="true"><use href="#i-chart"/></svg></span>
            <h3>Librarian reports</h3>
            <p>Total books, total members, active loans and suspended members on stat cards,
               with a clean availability chart for the monthly report.</p>
        </article>
    </div>
</section>

<%-- =========================== HOW IT WORKS ========================== --%>
<section class="container section">
    <div class="split">
        <div>
            <div class="section-head">
                <div>
                    <span class="eyebrow">How it works</span>
                    <h2>Three steps for a member</h2>
                </div>
            </div>
            <div class="steps">
                <div class="step">
                    <div>
                        <h3>Log in with your member ID</h3>
                        <p>Each member has a unique ID such as <span class="id-pill">M001</span> and a
                           BCrypt-hashed password. The password is never stored in plain text.</p>
                    </div>
                </div>
                <div class="step">
                    <div>
                        <h3>Search and issue a book</h3>
                        <p>Search the catalogue, press <em>Issue</em> and the system sets the 14-day due
                           date, reserves one copy and adds the loan to your list.</p>
                    </div>
                </div>
                <div class="step">
                    <div>
                        <h3>Return or renew before the due date</h3>
                        <p>Returning closes the loan and calculates the fine (if any). Renewing adds
                           7 days once, as long as the book is still on time.</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-head">
                <h2>The demo in 30 seconds</h2>
                <span class="badge badge-info">Demo</span>
            </div>
            <ul class="rule-list">
                <li>
                    <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>Librarian: <code>admin</code> / <code>admin123</code></span>
                </li>
                <li>
                    <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>Member: <code>M001</code> &hellip; <code>M004</code> / <code>member123</code></span>
                </li>
                <li>
                    <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>Loan of M002 is already 5 days late &rarr; live fine of Rs 10.</span>
                </li>
                <li>
                    <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>Member M004 is auto-suspended (book 101 days overdue).</span>
                </li>
                <li>
                    <svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                    <span>Use <strong>Simulated Today</strong> in the admin dashboard to jump forward in
                        time and show fines, renewals and suspension instantly.</span>
                </li>
            </ul>
            <div class="btn-row gap-top">
                <a class="btn btn-primary btn-sm" href="${ctx}/admin/login">Open admin panel</a>
                <a class="btn btn-ghost btn-sm" href="${ctx}/member/login">Open member area</a>
            </div>
        </div>
    </div>
</section>

<%-- ============================== CTA =============================== --%>
<section class="container section">
    <div class="card" style="background:linear-gradient(140deg,#0B3A7A,#1E6FE0);color:#fff;border:0">
        <div class="section-head" style="margin-bottom:.4rem">
            <div>
                <h2 style="color:#fff">Built for the Software Engineering course CIC-309</h2>
                <p style="color:#CFE1F8;margin:.4rem 0 0;max-width:70ch">
                    Model &ndash; View &ndash; Controller with a separate DAO layer, JDBC prepared
                    statements only, session-based authentication, role filters and JUnit tests for
                    every business rule.
                </p>
            </div>
            <a class="btn btn-light" href="${ctx}/about">
                Read the project details <svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg>
            </a>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
