package com.dtc.lms.support;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.model.Book;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory test double for {@link BookDAO}.
 *
 * <p>It behaves like the MySQL table: the book_id is unique, and
 * {@code decrementAvailable} refuses to go below zero. This lets us test the
 * service rules with a plain JUnit run - no database, no network, no setup for
 * the examiner.</p>
 */
public class InMemoryBookDAO implements BookDAO {

    private final Map<String, Book> table = new LinkedHashMap<>();

    @Override
    public void insert(Book book) throws SQLException {
        if (table.containsKey(book.getBookId())) {
            throw new SQLIntegrityConstraintViolationException(
                    "Duplicate entry for key 'PRIMARY' (book_id = " + book.getBookId() + ")");
        }
        table.put(book.getBookId(), book);
    }

    @Override
    public boolean existsById(String bookId) {
        return table.containsKey(bookId);
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return Optional.ofNullable(table.get(bookId));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public List<Book> search(String keyword) {
        String needle = keyword.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : table.values()) {
            if (book.getBookId().toLowerCase(Locale.ROOT).contains(needle)
                    || book.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public boolean decrementAvailable(String bookId) {
        Book book = table.get(bookId);
        if (book == null || book.getAvailableCopies() <= 0) {
            return false;
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return true;
    }

    @Override
    public void incrementAvailable(String bookId) {
        Book book = table.get(bookId);
        if (book != null && book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
    }

    @Override
    public int countTitles() {
        return table.size();
    }

    @Override
    public int sumTotalCopies() {
        return table.values().stream().mapToInt(Book::getTotalCopies).sum();
    }

    @Override
    public int sumAvailableCopies() {
        return table.values().stream().mapToInt(Book::getAvailableCopies).sum();
    }

    @Override
    public void deleteAll() {
        table.clear();
    }
}
