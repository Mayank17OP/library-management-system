package com.dtc.lms.dao;

import com.dtc.lms.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for the {@code books} table.
 *
 * <p>The interface keeps the services independent of JDBC: production code uses
 * {@link com.dtc.lms.dao.jdbc.JdbcBookDAO}, while the JUnit tests plug in a
 * small in-memory fake (see {@code src/test/java/.../support}).</p>
 *
 * <p>Every method may throw {@link SQLException}; services convert that into a
 * friendly error, so raw stack traces never reach the browser.</p>
 */
public interface BookDAO {

    /** Inserts a new book. Throws SQLException on duplicate book_id (primary key). */
    void insert(Book book) throws SQLException;

    boolean existsById(String bookId) throws SQLException;

    Optional<Book> findById(String bookId) throws SQLException;

    /** All books ordered by book_id. */
    List<Book> findAll() throws SQLException;

    /** Books whose id, title or author contains the keyword (case-insensitive). */
    List<Book> search(String keyword) throws SQLException;

    /**
     * Atomically reduces availability by one copy (Requirement 7).
     *
     * @return true when a copy was reserved, false when none was available.
     *         The check and the update happen in ONE SQL statement so two
     *         simultaneous issues can never take the last copy twice.
     */
    boolean decrementAvailable(String bookId) throws SQLException;

    /** Gives one copy back to the shelf after a return (Requirement 9). */
    void incrementAvailable(String bookId) throws SQLException;

    int countTitles() throws SQLException;

    int sumTotalCopies() throws SQLException;

    int sumAvailableCopies() throws SQLException;

    /** Used by the "Reset Demo Data" button. */
    void deleteAll() throws SQLException;
}
