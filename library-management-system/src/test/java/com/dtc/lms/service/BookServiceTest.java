package com.dtc.lms.service;

import com.dtc.lms.model.Book;
import com.dtc.lms.support.InMemoryBookDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for adding and searching books (Requirements 2, 3 and 4). */
class BookServiceTest {

    private InMemoryBookDAO bookDAO;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookDAO = new InMemoryBookDAO();
        bookService = new BookService(bookDAO);
    }

    @Test
    @DisplayName("A new book starts with all its copies available")
    void addBookSetsAvailableEqualsTotal() throws Exception {
        Book book = bookService.addBook("B100", "Effective Java", "Joshua Bloch", "3");

        assertEquals(3, book.getTotalCopies());
        assertEquals(3, book.getAvailableCopies());
        assertTrue(book.isAvailable());
        assertEquals(1, bookService.listAll().size());
    }

    @Test
    @DisplayName("A duplicate book ID is rejected")
    void duplicateBookIdIsRejected() throws Exception {
        bookService.addBook("B100", "Effective Java", "Joshua Bloch", "3");

        ServiceException error = assertThrows(ServiceException.class,
                () -> bookService.addBook("b100", "Another title", "Another author", "1"));

        assertTrue(error.getMessage().contains("already exists"), error.getMessage());
        assertEquals(1, bookService.listAll().size(), "the catalogue must not contain two B100 rows");
    }

    @Test
    @DisplayName("Invalid input is rejected before it reaches the database")
    void invalidInputIsRejected() {
        assertThrows(ServiceException.class,
                () -> bookService.addBook("", "Clean Code", "Robert C. Martin", "2"));
        assertThrows(ServiceException.class,
                () -> bookService.addBook("B101", "", "Robert C. Martin", "2"));
        assertThrows(ServiceException.class,
                () -> bookService.addBook("B101", "Clean Code", "Robert C. Martin", "0"));
        assertThrows(ServiceException.class,
                () -> bookService.addBook("B101", "Clean Code", "Robert C. Martin", "many"));
    }

    @Test
    @DisplayName("Search matches the ID, the title and the author")
    void searchByAllThreeColumns() throws Exception {
        bookService.addBook("B001", "Let Us Java", "Yashavant Kanetkar", "5");
        bookService.addBook("B002", "Clean Code", "Robert C. Martin", "3");

        assertEquals(1, bookService.search("B002").size());
        assertEquals(1, bookService.search("clean").size());
        assertEquals(1, bookService.search("kanetkar").size());
        assertEquals(2, bookService.search("").size(), "an empty keyword returns everything");
    }
}
