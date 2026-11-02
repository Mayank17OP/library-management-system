<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="About the project"/>
<%@ include file="/WEB-INF/views/includes/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="breadcrumb">Project details</p>
        <h1>About this project</h1>
        <p>A Library Management System developed as the Software Engineering (CIC-309) project at
           Delhi Technical Campus, Greater Noida.</p>
    </div>
</section>

<div class="container">
    <div class="split">
        <div class="grid" style="gap:1.5rem">

            <article class="card">
                <h2>Problem statement</h2>
                <p class="muted">
                    The college library kept its issue and return records in a paper register. Finding out
                    who has a book, when it is due, how much fine a student owes, or which students are
                    defaulters meant manual searching, and copies were counted by hand.
                </p>
                <p class="muted mb-0">
                    This system replaces that register. It keeps one record for every book, member and
                    loan, calculates fines using a fixed rule, suspends defaulters automatically and gives
                    the librarian a live report of the library.
                </p>
            </article>

            <article class="card">
                <h2>Modules</h2>
                <div class="grid grid-2">
                    <ul class="rule-list">
                        <li><svg class="icon" aria-hidden="true"><use href="#i-lock"/></svg>
                            <span><strong>Authentication</strong> &ndash; admin and member login, BCrypt
                            password hashing, session handling, role filter</span></li>
                        <li><svg class="icon" aria-hidden="true"><use href="#i-book"/></svg>
                            <span><strong>Catalogue</strong> &ndash; add book, list books, search by
                            ID / title / author</span></li>
                        <li><svg class="icon" aria-hidden="true"><use href="#i-users"/></svg>
                            <span><strong>Membership</strong> &ndash; register member, member list,
                            active / suspended status</span></li>
                    </ul>
                    <ul class="rule-list">
                        <li><svg class="icon" aria-hidden="true"><use href="#i-arrow-right"/></svg>
                            <span><strong>Circulation</strong> &ndash; issue, return, renew with
                            availability control</span></li>
                        <li><svg class="icon" aria-hidden="true"><use href="#i-clock"/></svg>
                            <span><strong>Fines &amp; suspension</strong> &ndash; Rs 2 per late day,
                            auto-suspend beyond 100 days</span></li>
                        <li><svg class="icon" aria-hidden="true"><use href="#i-chart"/></svg>
                            <span><strong>Reports</strong> &ndash; stat cards, overdue list,
                            availability chart</span></li>
                    </ul>
                </div>
            </article>

            <article class="card">
                <h2>Technology stack</h2>
                <div class="tag-row mb-2">
                    <span class="tag">Java 17</span>
                    <span class="tag">JSP + JSTL</span>
                    <span class="tag">Jakarta Servlets 6</span>
                    <span class="tag">JDBC</span>
                    <span class="tag">MySQL 8</span>
                    <span class="tag">Apache Tomcat 10.1</span>
                    <span class="tag">Maven</span>
                    <span class="tag">HikariCP</span>
                    <span class="tag">BCrypt</span>
                    <span class="tag">HTML5</span>
                    <span class="tag">CSS3</span>
                    <span class="tag">Vanilla JavaScript</span>
                    <span class="tag">JUnit 5</span>
                    <span class="tag">Docker</span>
                    <span class="tag">Git &amp; GitHub</span>
                </div>
                <p class="muted mb-0 small">
                    No Spring, no ORM: the request goes Servlet &rarr; Service &rarr; DAO &rarr; JDBC
                    &rarr; MySQL, which keeps every step visible during the viva.
                </p>
            </article>

            <article class="card">
                <h2>Architecture (MVC + DAO)</h2>
                <div class="table-wrap">
                    <table class="data">
                        <thead>
                        <tr><th>Layer</th><th>Responsibility</th><th>Classes</th></tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td class="cell-strong">model</td>
                            <td>Plain Java objects that map to database rows</td>
                            <td>Book, Member, Loan, MemberStatus, Role</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">dao</td>
                            <td>SQL only, PreparedStatement, no business rules</td>
                            <td>BookDAO, MemberDAO, LoanDAO, SettingsDAO (+ JDBC implementations)</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">service</td>
                            <td>Business rules: fines, suspension, issue/return/renew</td>
                            <td>AuthService, BookService, CirculationService, FineService,
                                ReportService, ClockService, DemoDataService</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">controller</td>
                            <td>Reads the request, calls a service, chooses the view</td>
                            <td>Login, Logout, Book, Member, Circulation, Report, Settings, Home servlets</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">filter / util</td>
                            <td>Cross-cutting concerns</td>
                            <td>AuthFilter (role check), Utf8Filter, DBConnection, PasswordUtil, AppConfig</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">view</td>
                            <td>JSP + JSTL templates, shared header/footer</td>
                            <td>home, about, logins, admin/*, member/*, error/*</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </article>

            <article class="card">
                <h2>Database design</h2>
                <div class="table-wrap">
                    <table class="data">
                        <thead>
                        <tr><th>Table</th><th>Key columns</th><th>Purpose</th></tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td class="cell-strong">books</td>
                            <td>book_id (PK), title, author, total_copies, available_copies</td>
                            <td>Catalogue and live availability</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">members</td>
                            <td>member_id (PK), name, password_hash, status</td>
                            <td>Accounts and suspension state</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">loans</td>
                            <td>loan_id (PK), member_id (FK), book_id (FK), issue_date, due_date,
                                return_date, returned, renewed, fine</td>
                            <td>One row per issue transaction</td>
                        </tr>
                        <tr>
                            <td class="cell-strong">app_settings</td>
                            <td>setting_key (PK), setting_value</td>
                            <td>Demo "Simulated Today" date</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </article>
        </div>

        <aside class="grid" style="gap:1.5rem">
            <article class="card">
                <div class="card-head">
                    <h2>Project team</h2>
                    <span class="badge badge-info">Group</span>
                </div>
                <dl class="kv">
                    <dt>Anushka Bhatnagar</dt>
                    <dd>04118002724</dd>
                    <dt>Bhawana Joshi</dt>
                    <dd>04218002724</dd>
                    <dt>Guide</dt>
                    <dd>Dr. Eirtyy Telang</dd>
                    <dt>Subject</dt>
                    <dd>Software Engineering (CIC-309)</dd>
                    <dt>Course</dt>
                    <dd>B.Tech CSE, 3rd Year, 5th Semester, Section A</dd>
                    <dt>Session</dt>
                    <dd>2026-27</dd>
                    <dt>Institute</dt>
                    <dd>Delhi Technical Campus, Greater Noida</dd>
                </dl>
            </article>

            <article class="card">
                <div class="card-head">
                    <h2>Business rules</h2>
                    <svg class="icon icon-lg" style="color:#1E6FE0" aria-hidden="true"><use href="#i-shield"/></svg>
                </div>
                <ul class="rule-list">
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Loan period: <strong>14 days</strong></span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Fine: <strong>Rs 2 per late day</strong></span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Renewal: <strong>one time, +7 days</strong>, only if not overdue</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Suspension: book more than <strong>100 days</strong> overdue</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Issue: only if <strong>available &gt; 0</strong> and member active</span></li>
                </ul>
                <p class="tiny muted mb-0" style="margin-top:.9rem">
                    All five values live in one class, <code>AppConfig</code>, so the rules can be changed
                    without touching any other file.
                </p>
            </article>

            <article class="card">
                <div class="card-head">
                    <h2>Quality checks</h2>
                    <svg class="icon icon-lg" style="color:#12855B" aria-hidden="true"><use href="#i-check-circle"/></svg>
                </div>
                <ul class="rule-list">
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>JUnit 5 tests for fines, suspension, issue, return and renew</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Prepared statements only &ndash; no SQL injection</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Passwords hashed with BCrypt</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Server-side validation for every form</span></li>
                    <li><svg class="icon" aria-hidden="true"><use href="#i-check"/></svg>
                        <span>Custom 404 / 500 pages &ndash; no stack traces</span></li>
                </ul>
            </article>
        </aside>
    </div>
</div>

<%@ include file="/WEB-INF/views/includes/footer.jsp" %>
