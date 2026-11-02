package com.dtc.lms.dao.jdbc;

import com.dtc.lms.dao.BookDAO;
import com.dtc.lms.model.Book;
import com.dtc.lms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link BookDAO}.
 *
 * <p>Only {@link PreparedStatement} is used. A prepared statement keeps the SQL
 * text and the user data separate, which is how SQL injection is prevented
 * (a value such as {@code ' OR 1=1 --} is treated as plain text, never as SQL).</p>
 */
public class JdbcBookDAO implements BookDAO {

    private static final String SELECT_COLUMNS =
            "SELECT book_id, title, author, total_copies, available_copies FROM books ";

    @Override
    public void insert(Book book) throws SQLException {
        String sql = "INSERT INTO books (book_id, title, author, total_copies, available_copies) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsById(String bookId) throws SQLException {
        String sql = "SELECT 1 FROM books WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Optional<Book> findById(String bookId) throws SQLException {
        String sql = SELECT_COLUMNS + "WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Book> findAll() throws SQLException {
        String sql = SELECT_COLUMNS + "ORDER BY book_id";
        List<Book> books = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> search(String keyword) throws SQLException {
        String sql = SELECT_COLUMNS
                + "WHERE LOWER(book_id) LIKE ? OR LOWER(title) LIKE ? OR LOWER(author) LIKE ? "
                + "ORDER BY book_id";
        String like = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
        List<Book> books = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        }
        return books;
    }

    @Override
    public boolean decrementAvailable(String bookId) throws SQLException {
        // The "available_copies > 0" test is part of the UPDATE itself, so the
        // database guarantees we never issue a copy that is not on the shelf.
        String sql = "UPDATE books SET available_copies = available_copies - 1 "
                + "WHERE book_id = ? AND available_copies > 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public void incrementAvailable(String bookId) throws SQLException {
        // "available_copies < total_copies" protects the data from a double return.
        String sql = "UPDATE books SET available_copies = available_copies + 1 "
                + "WHERE book_id = ? AND available_copies < total_copies";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.executeUpdate();
        }
    }

    @Override
    public int countTitles() throws SQLException {
        return singleInt("SELECT COUNT(*) FROM books");
    }

    @Override
    public int sumTotalCopies() throws SQLException {
        return singleInt("SELECT COALESCE(SUM(total_copies), 0) FROM books");
    }

    @Override
    public int sumAvailableCopies() throws SQLException {
        return singleInt("SELECT COALESCE(SUM(available_copies), 0) FROM books");
    }

    @Override
    public void deleteAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM books")) {
            ps.executeUpdate();
        }
    }

    /* ----------------------------- helpers ----------------------------- */

    private int singleInt(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"));
    }
}
