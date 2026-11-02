package com.dtc.lms.controller;

import com.dtc.lms.model.Book;
import com.dtc.lms.service.BookService;
import com.dtc.lms.service.ServiceException;
import com.dtc.lms.util.Flash;
import com.dtc.lms.util.Validators;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Admin book management: add a book (Requirement 2), list all books
 * (Requirement 3) and search by ID, title or author (Requirement 4).
 */
@WebServlet("/admin/books")
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = Validators.trim(request.getParameter("q"));
        try {
            List<Book> books = keyword.isEmpty() ? bookService.listAll() : bookService.search(keyword);
            request.setAttribute("books", books);
            request.setAttribute("keyword", keyword);
        } catch (ServiceException ex) {
            Flash.error(request, ex.getMessage());
            request.setAttribute("books", List.of());
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = Validators.trim(request.getParameter("action"));

        if ("add".equals(action)) {
            try {
                Book book = bookService.addBook(
                        request.getParameter("bookId"),
                        request.getParameter("title"),
                        request.getParameter("author"),
                        request.getParameter("totalCopies"));
                Flash.success(request, "Book added: " + book.getBookId() + " - " + book.getTitle()
                        + " (" + book.getTotalCopies() + " copies, all available).");
            } catch (ServiceException ex) {
                Flash.error(request, ex.getMessage());
            }
        } else {
            Flash.error(request, "Unknown action.");
        }

        // Post/Redirect/Get: refreshing the page will not add the book twice.
        response.sendRedirect(request.getContextPath() + "/admin/books");
    }
}
