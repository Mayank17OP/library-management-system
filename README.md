# library-management-system

A simple, static library management system: manage books, members, loans, returns and overdue fines.

## Features
- **Books:** add, edit, delete and search by title, author or ISBN; track copies on loan.
- **Members:** register and remove members (members with books out cannot be removed).
- **Loans:** check out books with a loan period, mark returns, and see overdue status and fines (configurable per-day rate).

Data is stored in the browser's `localStorage`, so there is no backend to run.

## Run locally
Open `index.html` in a browser, or serve the folder:
```bash
python3 -m http.server 8000
```

## Deployment
Deployed to GitHub Pages via the workflow in `.github/workflows/pages.yml`.
