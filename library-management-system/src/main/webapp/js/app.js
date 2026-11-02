/* ==========================================================================
   Library Management System - client-side behaviour (vanilla JavaScript)
   --------------------------------------------------------------------------
   This file only improves the user experience. Every rule is enforced again
   on the server, because JavaScript can be disabled or bypassed.
   ========================================================================== */
(function () {
    "use strict";

    /* -----------------------------------------------------------------
       1. Mobile navigation toggle
       ----------------------------------------------------------------- */
    var navToggle = document.getElementById("navToggle");
    var navMenu = document.getElementById("navMenu");

    if (navToggle && navMenu) {
        navToggle.addEventListener("click", function () {
            var open = navMenu.classList.toggle("is-open");
            navToggle.setAttribute("aria-expanded", open ? "true" : "false");
        });
    }

    /* -----------------------------------------------------------------
       2. Flash messages fade out after a few seconds
       ----------------------------------------------------------------- */
    document.querySelectorAll(".alert").forEach(function (alert) {
        window.setTimeout(function () {
            alert.style.transition = "opacity .5s ease, transform .5s ease";
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-6px)";
            window.setTimeout(function () { alert.remove(); }, 520);
        }, 9000);
    });

    /* -----------------------------------------------------------------
       3. "Are you sure?" on destructive forms (reset demo data, return...)
       ----------------------------------------------------------------- */
    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!window.confirm(form.getAttribute("data-confirm"))) {
                event.preventDefault();
            }
        });
    });

    /* -----------------------------------------------------------------
       4. Light client-side validation (server validates again)
       ----------------------------------------------------------------- */
    var ID_PATTERN = /^[A-Za-z0-9_-]{2,12}$/;

    function showFieldError(input, message) {
        input.classList.add("has-error");
        input.style.borderColor = "#C0272D";
        var note = input.parentElement.querySelector(".field-error");
        if (!note) {
            note = document.createElement("span");
            note.className = "field-error tiny";
            note.style.color = "#C0272D";
            input.parentElement.appendChild(note);
        }
        note.textContent = message;
    }

    function clearFieldError(input) {
        input.classList.remove("has-error");
        input.style.borderColor = "";
        var note = input.parentElement.querySelector(".field-error");
        if (note) { note.textContent = ""; }
    }

    document.querySelectorAll("form[data-validate]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            var valid = true;

            form.querySelectorAll("input[data-rule]").forEach(function (input) {
                var value = (input.value || "").trim();
                var rule = input.getAttribute("data-rule");
                clearFieldError(input);

                if (input.required && value === "") {
                    showFieldError(input, input.getAttribute("data-label") + " is required.");
                    valid = false;
                    return;
                }
                if (value === "") { return; }

                if (rule === "id" && !ID_PATTERN.test(value)) {
                    showFieldError(input, "Use 2-12 letters or digits (e.g. B013).");
                    valid = false;
                } else if (rule === "name" && value.length < 2) {
                    showFieldError(input, "Please enter at least 2 characters.");
                    valid = false;
                } else if (rule === "positive" && (isNaN(Number(value)) || Number(value) < 1)) {
                    showFieldError(input, "Enter a number greater than zero.");
                    valid = false;
                } else if (rule === "password" && value.length < 6) {
                    showFieldError(input, "At least 6 characters.");
                    valid = false;
                }
            });

            // password confirmation
            var password = form.querySelector("input[name='password']");
            var confirm = form.querySelector("input[name='confirmPassword']");
            if (password && confirm && confirm.value !== password.value) {
                showFieldError(confirm, "The two passwords do not match.");
                valid = false;
            }

            if (!valid) {
                event.preventDefault();
            }
        });
    });

    /* -----------------------------------------------------------------
       5. Prevent double submission (double-click on "Issue" / "Add")
       ----------------------------------------------------------------- */
    document.querySelectorAll("form").forEach(function (form) {
        form.addEventListener("submit", function () {
            var button = form.querySelector("button[type='submit']");
            if (button && !form.hasAttribute("data-confirm")) {
                window.setTimeout(function () {
                    button.disabled = true;
                    button.classList.add("is-disabled");
                }, 30);
            }
        });
    });

    /* -----------------------------------------------------------------
       6. Quick filter for long tables (e.g. the loan register)
       ----------------------------------------------------------------- */
    var tableFilter = document.querySelector("[data-table-filter]");
    if (tableFilter) {
        tableFilter.addEventListener("input", function () {
            var needle = tableFilter.value.toLowerCase().trim();
            document.querySelectorAll(tableFilter.getAttribute("data-table-filter") + " tbody tr")
                .forEach(function (row) {
                    row.style.display = row.textContent.toLowerCase().indexOf(needle) > -1 ? "" : "none";
                });
        });
    }

    /* -----------------------------------------------------------------
       7. Show the real date in the simulated-date banner
       ----------------------------------------------------------------- */
    var realToday = document.getElementById("realToday");
    if (realToday) {
        realToday.textContent = new Date().toISOString().slice(0, 10);
    }

    /* -----------------------------------------------------------------
       8. Copy-to-clipboard for the demo credentials hint
       ----------------------------------------------------------------- */
    document.querySelectorAll("[data-copy]").forEach(function (button) {
        button.addEventListener("click", function () {
            var text = button.getAttribute("data-copy");
            if (navigator.clipboard) {
                navigator.clipboard.writeText(text);
                var original = button.textContent;
                button.textContent = "Copied!";
                window.setTimeout(function () { button.textContent = original; }, 1500);
            }
        });
    });
})();
