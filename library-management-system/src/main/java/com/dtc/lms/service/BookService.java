package com.dtc.lms.service;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.dao.jdbc.JdbcBookDAO;
import com.dtc.lms.model.Book;
import com.dtc.lms.util.Validators;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Book use-cases: add a book (Requirement 2), list all books (Requirement 3)
 * and search a book (Requirement 4).
 *
 * <p>The service validates the input, applies the business rules and converts
 * database errors into user-friendly {@link ServiceException}s.</p>
 */
public class BookService {

    private final BookDAO bookDAO;

    /** Production constructor: uses the real JDBC DAO. */
    public BookService() {
        this(new JdbcBookDAO());
    }

    /** Test constructor: allows an in-memory DAO to be injected. */
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Adds a new title to the catalogue.
     *
     * <p>When a book is added, all its copies are on the shelf, i.e.
     * {@code available_copies = total_copies}.</p>
     *
     * @throws ServiceException when the input is invalid or the ID already exists
     */
    public Book addBook(String bookId, String title, String author, String totalCopies)
            throws ServiceException {

        String cleanId = Validators.trim(bookId).toUpperCase();
        try {
            cleanId = Validators.requireId(bookId, "Book ID");
            String cleanTitle = Validators.requireName(title, "Title");
            String cleanAuthor = Validators.requireName(author, "Author");
            int copies = Validators.requirePositiveInt(totalCopies, "Total copies");

            if (bookDAO.existsById(cleanId)) {
                throw new ServiceException(
                        "Book ID " + cleanId + " already exists. Please choose another ID.");
            }
            Book book = new Book(cleanId, cleanTitle, cleanAuthor, copies, copies);
            bookDAO.insert(book);
            return book;
        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException invalidInput) {
            // Validation problems are normal user errors, not crashes.
            throw new ServiceException(invalidInput.getMessage(), invalidInput);
        } catch (SQLIntegrityConstraintViolationException duplicateKey) {
            // Two librarians saving the same ID at the same moment: the PRIMARY KEY wins.
            throw new ServiceException("Book ID " + cleanId + " already exists.", duplicateKey);
        } catch (SQLException ex) {
            throw new ServiceException("Could not save the book. Please try again.", ex);
        }
    }

    /** All books in the catalogue (Requirement 3). */
    public List<Book> listAll() throws ServiceException {
        try {
            return bookDAO.findAll();
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the book list.", ex);
        }
    }

    /** Search by book ID, title or author (Requirement 4). */
    public List<Book> search(String keyword) throws ServiceException {
        String cleaned = Validators.trim(keyword);
        if (cleaned.isEmpty()) {
            return listAll();
        }
        try {
            return bookDAO.search(cleaned);
        } catch (SQLException ex) {
            throw new ServiceException("Could not search the catalogue.", ex);
        }
    }

    /** Exact lookup used by the issue screen. */
    public Book requireBook(String bookId) throws ServiceException {
        try {
            String cleanId = Validators.requireId(bookId, "Book ID");
            return bookDAO.findById(cleanId)
                    .orElseThrow(() -> new ServiceException("No book found with ID " + cleanId + "."));
        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException invalidInput) {
            throw new ServiceException(invalidInput.getMessage(), invalidInput);
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the book.", ex);
        }
    }

    public int countTitles() throws ServiceException {
        try {
            return bookDAO.countTitles();
        } catch (SQLException ex) {
            throw new ServiceException("Could not count the books.", ex);
        }
    }
}
