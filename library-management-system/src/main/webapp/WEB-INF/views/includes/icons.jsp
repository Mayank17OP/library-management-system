<%--
    Inline SVG icon sprite (no icon font, no external request).
    Every icon is drawn with "currentColor" strokes so it inherits the text
    colour of its parent and stays readable in every theme colour.
    Usage:  <svg class="icon" aria-hidden="true"><use href="#i-book"/></svg>
--%>
<svg xmlns="http://www.w3.org/2000/svg" style="display:none" aria-hidden="true" focusable="false">
    <symbol id="i-book" viewBox="0 0 24 24">
        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
    </symbol>
    <symbol id="i-book-open" viewBox="0 0 24 24">
        <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
        <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
    </symbol>
    <symbol id="i-menu" viewBox="0 0 24 24">
        <line x1="4" y1="7" x2="20" y2="7"/>
        <line x1="4" y1="12" x2="20" y2="12"/>
        <line x1="4" y1="17" x2="20" y2="17"/>
    </symbol>
    <symbol id="i-x" viewBox="0 0 24 24">
        <line x1="6" y1="6" x2="18" y2="18"/>
        <line x1="18" y1="6" x2="6" y2="18"/>
    </symbol>
    <symbol id="i-check" viewBox="0 0 24 24">
        <polyline points="20 6 9 17 4 12"/>
    </symbol>
    <symbol id="i-check-circle" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9"/>
        <polyline points="8.5 12.5 11 15 16 9.5"/>
    </symbol>
    <symbol id="i-alert-triangle" viewBox="0 0 24 24">
        <path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
        <line x1="12" y1="9" x2="12" y2="13"/>
        <line x1="12" y1="17" x2="12.01" y2="17"/>
    </symbol>
    <symbol id="i-alert-circle" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9"/>
        <line x1="12" y1="8" x2="12" y2="12.5"/>
        <line x1="12" y1="16" x2="12.01" y2="16"/>
    </symbol>
    <symbol id="i-info" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9"/>
        <line x1="12" y1="11" x2="12" y2="16.5"/>
        <line x1="12" y1="8" x2="12.01" y2="8"/>
    </symbol>
    <symbol id="i-user" viewBox="0 0 24 24">
        <circle cx="12" cy="8" r="4"/>
        <path d="M5.5 21a6.5 6.5 0 0 1 13 0"/>
    </symbol>
    <symbol id="i-users" viewBox="0 0 24 24">
        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
        <circle cx="9" cy="7" r="4"/>
        <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
    </symbol>
    <symbol id="i-lock" viewBox="0 0 24 24">
        <rect x="4" y="10.5" width="16" height="10.5" rx="2.5"/>
        <path d="M8 10.5V7a4 4 0 0 1 8 0v3.5"/>
    </symbol>
    <symbol id="i-search" viewBox="0 0 24 24">
        <circle cx="11" cy="11" r="7"/>
        <line x1="21" y1="21" x2="16.65" y2="16.65"/>
    </symbol>
    <symbol id="i-plus" viewBox="0 0 24 24">
        <line x1="12" y1="5" x2="12" y2="19"/>
        <line x1="5" y1="12" x2="19" y2="12"/>
    </symbol>
    <symbol id="i-refresh" viewBox="0 0 24 24">
        <path d="M20.5 12a8.5 8.5 0 1 1-2.6-6.1"/>
        <polyline points="20.5 4 20.5 9.5 15 9.5"/>
    </symbol>
    <symbol id="i-undo" viewBox="0 0 24 24">
        <polyline points="9 14 4 9 9 4"/>
        <path d="M4 9h9.5a6.5 6.5 0 0 1 0 13H9"/>
    </symbol>
    <symbol id="i-chart" viewBox="0 0 24 24">
        <line x1="18" y1="20" x2="18" y2="10"/>
        <line x1="12" y1="20" x2="12" y2="4"/>
        <line x1="6" y1="20" x2="6" y2="14"/>
    </symbol>
    <symbol id="i-clock" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9"/>
        <polyline points="12 7 12 12 16 14"/>
    </symbol>
    <symbol id="i-calendar" viewBox="0 0 24 24">
        <rect x="3" y="5" width="18" height="16" rx="2.5"/>
        <line x1="3" y1="10" x2="21" y2="10"/>
        <line x1="8" y1="3" x2="8" y2="7"/>
        <line x1="16" y1="3" x2="16" y2="7"/>
    </symbol>
    <symbol id="i-reset" viewBox="0 0 24 24">
        <path d="M3 12a9 9 0 1 0 3-6.7"/>
        <polyline points="3 3 3 9 9 9"/>
    </symbol>
    <symbol id="i-logout" viewBox="0 0 24 24">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
        <polyline points="16 17 21 12 16 7"/>
        <line x1="21" y1="12" x2="9" y2="12"/>
    </symbol>
    <symbol id="i-login" viewBox="0 0 24 24">
        <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
        <polyline points="10 17 15 12 10 7"/>
        <line x1="15" y1="12" x2="3" y2="12"/>
    </symbol>
    <symbol id="i-shield" viewBox="0 0 24 24">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
        <polyline points="9 11.5 11.5 14 15.5 9.5"/>
    </symbol>
    <symbol id="i-database" viewBox="0 0 24 24">
        <ellipse cx="12" cy="5" rx="8.5" ry="3"/>
        <path d="M3.5 5v14c0 1.66 3.8 3 8.5 3s8.5-1.34 8.5-3V5"/>
        <path d="M3.5 12c0 1.66 3.8 3 8.5 3s8.5-1.34 8.5-3"/>
    </symbol>
    <symbol id="i-code" viewBox="0 0 24 24">
        <polyline points="16 18 22 12 16 6"/>
        <polyline points="8 6 2 12 8 18"/>
    </symbol>
    <symbol id="i-arrow-right" viewBox="0 0 24 24">
        <line x1="4" y1="12" x2="19" y2="12"/>
        <polyline points="13 6 19 12 13 18"/>
    </symbol>
    <symbol id="i-home" viewBox="0 0 24 24">
        <path d="M3 9.5 12 3l9 6.5V20a1 1 0 0 1-1 1h-5v-6H10v6H4a1 1 0 0 1-1-1z"/>
    </symbol>
    <symbol id="i-list" viewBox="0 0 24 24">
        <line x1="8" y1="6" x2="21" y2="6"/>
        <line x1="8" y1="12" x2="21" y2="12"/>
        <line x1="8" y1="18" x2="21" y2="18"/>
        <line x1="3.5" y1="6" x2="3.51" y2="6"/>
        <line x1="3.5" y1="12" x2="3.51" y2="12"/>
        <line x1="3.5" y1="18" x2="3.51" y2="18"/>
    </symbol>
    <symbol id="i-star" viewBox="0 0 24 24">
        <polygon points="12 3 14.9 9 21.5 9.8 16.7 14.4 17.9 21 12 17.8 6.1 21 7.3 14.4 2.5 9.8 9.1 9"/>
    </symbol>
    <symbol id="i-filter" viewBox="0 0 24 24">
        <polygon points="3 5 21 5 14 13.5 14 20 10 18 10 13.5"/>
    </symbol>
    <symbol id="i-printer" viewBox="0 0 24 24">
        <path d="M7 9V4h10v5"/>
        <rect x="4" y="9" width="16" height="7" rx="2"/>
        <path d="M7 14h10v6H7z"/>
    </symbol>
    <symbol id="i-graduation" viewBox="0 0 24 24">
        <path d="M2 8.5 12 4l10 4.5-10 4.5z"/>
        <path d="M6 11v5.2c0 1.3 2.7 2.8 6 2.8s6-1.5 6-2.8V11"/>
    </symbol>
</svg>
