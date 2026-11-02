<%-- Shared footer + scripts. Closes the <main> opened in header.jsp. --%>
</main>

<footer class="site-footer">
    <div class="container footer-inner">
        <div class="footer-top">
            <div class="footer-brand">
                <span class="brand-mark"><svg class="icon" aria-hidden="true"><use href="#i-book-open"/></svg></span>
                <div>
                    <strong>Library Management System</strong>
                    <span>Automating book issue, return, renewal, fines and reports.</span>
                </div>
            </div>
            <nav class="footer-links" aria-label="Footer navigation">
                <a href="${ctx}/home">Home</a>
                <a href="${ctx}/about">About the project</a>
                <a href="${ctx}/member/login">Member login</a>
                <a href="${ctx}/admin/login">Librarian login</a>
            </nav>
        </div>

        <%-- Required footer line --%>
        <p class="footer-credit mb-0">
            Delhi Technical Campus | Software Engineering CIC-309 | Anushka Bhatnagar and Bhawana Joshi
        </p>
        <p class="footer-note">
            Guide: Dr. Eirtyy Telang &middot; B.Tech CSE, 3rd Year, 5th Semester, Section A, Session 2026-27 &middot;
            Built with Java 17, JSP, Jakarta Servlets, JDBC and MySQL 8
        </p>
    </div>
</footer>

<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
