package com.libraryapp.uts.Model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class LoanTableModel extends AbstractTableModel {
    private List<Loan> loans;
    private final String[] columnNames = {"Title", "Author", "Username", "Loan Date", "Return Date"};

    public LoanTableModel(List<Loan> loans) {
        this.loans = loans;
    }

    @Override
    public int getRowCount() {
        return loans.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Loan loan = loans.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return loan.getTitle();
            case 1:
                return loan.getAuthor();
            case 2:
                return loan.getUsername();
            case 3:
                return loan.getLoanDate();
            case 4:
                return loan.getReturnDate();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
        fireTableDataChanged();
    }

    public Loan getLoanAt(int rowIndex) {
        return loans.get(rowIndex);
    }
}
