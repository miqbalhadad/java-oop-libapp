package com.libraryapp.uts.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books;
    private List<BookAggregated> aggregatedBooks;
    private final String[] columnNames = {"Title", "Author", "Jumlah Buku"};

    public BookTableModel(List<Book> books) {
        this.books = books;
        aggregateBooks();
    }

    @Override
    public int getRowCount() {
        return aggregatedBooks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BookAggregated bookAggregated = aggregatedBooks.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return bookAggregated.getTitle();
            case 1:
                return bookAggregated.getAuthor();
            case 2:
                return bookAggregated.getCount();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    private void aggregateBooks() {
        aggregatedBooks = new ArrayList<>();
        Map<String, BookAggregated> titleToAggregatedBook = new HashMap<>();

        for (Book book : books) {
            String title = book.getTitle();
            String author = book.getAuthor();

            if (titleToAggregatedBook.containsKey(title)) {
                BookAggregated aggregatedBook = titleToAggregatedBook.get(title);
                aggregatedBook.incrementCount();
            } else {
                BookAggregated aggregatedBook = new BookAggregated(title, author);
                aggregatedBooks.add(aggregatedBook);
                titleToAggregatedBook.put(title, aggregatedBook);
            }
        }
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        aggregateBooks();
        fireTableDataChanged();
    }

    public void addBook(Book book) {
        books.add(book);
        aggregateBooks();
        fireTableDataChanged();
    }

    public void updateBook(int rowIndex, Book book) {
        books.set(rowIndex, book);
        aggregateBooks();
        fireTableDataChanged();
    }

    public void removeBook(int rowIndex) {
        books.remove(rowIndex);
        aggregateBooks();
        fireTableDataChanged();
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBook(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < books.size()) {
            return books.get(rowIndex);
        }
        return null;
    }

    public BookAggregated getAggregatedBook(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < aggregatedBooks.size()) {
            return aggregatedBooks.get(rowIndex);
        }
        return null;
    }

    public void clear() {
        books.clear();
        aggregatedBooks.clear();
        fireTableDataChanged();
    }
}
