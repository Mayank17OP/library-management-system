package com.dtc.lms.model;

/**
 * A book title held by the library.
 *
 * <p>Maps to the {@code books} table:</p>
 * <pre>
 * books(book_id, title, author, total_copies, available_copies)
 * </pre>
 */
public class Book {

    private String bookId;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public Book() {
    }

    public Book(String bookId, String title, String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    /** True when at least one copy is on the shelf (Requirement 7). */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /** Number of copies currently with members. */
    public int getIssuedCopies() {
        return totalCopies - availableCopies;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "Book{" + bookId + ", '" + title + "', total=" + totalCopies
                + ", available=" + availableCopies + '}';
    }
}
