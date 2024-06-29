package com.libraryapp.uts.Model;

import java.time.LocalDate;

public class Loan {
    private int id;
    private String title;
    private String author;
    private String username;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public Loan(int id, String title, String author, LocalDate loanDate, LocalDate returnDate, boolean isReturned) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    @Override
    public String toString() {
        return title + " by " + author + " (Loaned by: " + username + " on: " + loanDate + ", Returned: " + isReturned + ")";
    }
}
