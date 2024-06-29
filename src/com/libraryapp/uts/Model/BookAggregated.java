package com.libraryapp.uts.Model;

public class BookAggregated {
    private String title;
    private String author;
    private int count;

    public BookAggregated(String title, String author) {
        this.title = title;
        this.author = author;
        this.count = 1; // Start with count 1 for the first occurrence
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count++;
    }
}
