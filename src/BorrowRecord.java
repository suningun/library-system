import java.time.LocalDate;

public class BorrowRecord {
    private final String memberId;
    private final String memberName;
    private final String bookTitle;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(String memberId, String memberName, String bookTitle, LocalDate borrowDate, LocalDate dueDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    // ...existing code...
    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    // ===== CHECK OVERDUE =====
    public boolean isOverdue() {
        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();
        return endDate.isAfter(dueDate);
    }

    // ===== GET OVERDUE DAYS =====
    public long getOverdueDays() {
        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();

        if (endDate.isAfter(dueDate)) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, endDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Member: [%s] %s | Book: %s | Borrowed: %s | Due: %s | Returned: %s",
                memberId, memberName, bookTitle, borrowDate, dueDate, returnDate != null ? returnDate : "Not returned");
    }
}

