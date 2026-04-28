import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnBook {

    private ArrayList<BorrowRecord> borrowRecords = new ArrayList<>();
    private final Scanner scanner;
    private final BookManagement bookManagement;
    private final MemberManagement memberManagement;
    private final FineCalculator fineCalculator;

    private static final String FILE_NAME = "borrowRecord.json";

    public ReturnBook(Scanner scanner, BookManagement bookManagement, MemberManagement memberManagement) {
        this.scanner = scanner;
        this.bookManagement = bookManagement;
        this.memberManagement = memberManagement;
        this.fineCalculator = new FineCalculator();
    }

    // 🔹 MAIN RUN METHOD
    public void run() {
        loadBorrowRecords();

        // Ensure member and book data are loaded
        memberManagement.loadMembersData();
        bookManagement.loadBooksData();

        try {
            System.out.println("\n--- Return Book ---");

            // Show active borrow records first
            ArrayList<BorrowRecord> activeRecords = getActiveRecords();
            if (activeRecords.isEmpty()) {
                System.out.println("No active borrowed books to return.");
                return;
            }

            System.out.println("\nActive Borrowed Books:");
            for (int i = 0; i < activeRecords.size(); i++) {
                System.out.println((i + 1) + ". " + activeRecords.get(i));
            }

            System.out.print("\nEnter Member ID: ");
            String memberId = scanner.nextLine().trim();

            System.out.print("Enter Book Title: ");
            String bookTitle = scanner.nextLine().trim();

            // Check if member exists
            String memberName = getMemberName(memberId);
            if (memberName == null) {
                System.out.println("Member not found.");
                return;
            }

            // Find the borrow record
            BorrowRecord record = findActiveBorrowRecord(memberId, bookTitle);
            if (record == null) {
                System.out.println("This member has not borrowed this book, or it has already been returned.");
                return;
            }

            // Check if there's any overdue and calculate fine if needed
            LocalDate today = LocalDate.now();
            boolean isOverdue = record.isOverdue();
            long daysOverdue = record.getOverdueDays();
            int fine = fineCalculator.calculateFine(daysOverdue);

            // Update return date
            record.setReturnDate(today);
            
            // Increase available copies
            returnBookCopy(bookTitle);
            
            saveBorrowRecords();
            // Save updated book stock to JSON
            bookManagement.saveBooksData();

            System.out.println("\n✓ Book returned successfully!");
            System.out.println("Member: [" + memberId + "] " + memberName);
            System.out.println("Book Title: " + bookTitle);
            System.out.println("Borrow Date: " + record.getBorrowDate());
            System.out.println("Due Date: " + record.getDueDate());
            System.out.println("Return Date: " + today);

            if (isOverdue) {
                System.out.println("\n⚠ OVERDUE NOTICE:");
                System.out.println("Days Overdue: " + daysOverdue + " days");
                System.out.println("Fine Due: $" + fine);
            } else {
                System.out.println("\n✓ Returned on time!");
            }

        } catch (Exception e) {
            System.out.println("Error during return process: " + e.getMessage());
        }
    }

    // 🔹 GET ACTIVE BORROW RECORDS
    private ArrayList<BorrowRecord> getActiveRecords() {
        ArrayList<BorrowRecord> activeRecords = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null) {
                activeRecords.add(record);
            }
        }
        return activeRecords;
    }

    // 🔹 FIND ACTIVE BORROW RECORD BY MEMBER ID AND BOOK TITLE
    private BorrowRecord findActiveBorrowRecord(String memberId, String bookTitle) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getMemberId().equalsIgnoreCase(memberId) &&
                record.getBookTitle().equalsIgnoreCase(bookTitle) &&
                record.getReturnDate() == null) {
                return record;
            }
        }
        return null;
    }

    // 🔹 GET MEMBER NAME BY ID (SIMPLIFIED)
    private String getMemberName(String memberId) {
        ArrayList<Member> memberList = memberManagement.getMemberList();
        for (Member m : memberList) {
            if (m.getId().equalsIgnoreCase(memberId)) {
                return m.getName();
            }
        }
        return null;
    }

    // 🔹 GET OVERDUE RECORDS
    public void viewOverdueRecords() {
        loadBorrowRecords();

        System.out.println("\n===== Overdue Books =====");

        ArrayList<BorrowRecord> overdueRecords = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null && today.isAfter(record.getDueDate())) {
                overdueRecords.add(record);
            }
        }

        if (overdueRecords.isEmpty()) {
            System.out.println("No overdue books.");
            return;
        }

        for (int i = 0; i < overdueRecords.size(); i++) {
            BorrowRecord record = overdueRecords.get(i);
            long daysOverdue = record.getOverdueDays();
            int fine = fineCalculator.calculateFine(daysOverdue);

            System.out.println((i + 1) + ". " + record);
            System.out.println("   Days Overdue: " + daysOverdue + " | Fine: $" + fine);
        }
    }

    // SAVE JSON
    private void saveBorrowRecords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("[\n");
            for (int i = 0; i < borrowRecords.size(); i++) {
                BorrowRecord record = borrowRecords.get(i);
                writer.write("  {\n");
                writer.write("    \"memberId\": \"" + JsonUtility.escapeJson(record.getMemberId()) + "\",\n");
                writer.write("    \"memberName\": \"" + JsonUtility.escapeJson(record.getMemberName()) + "\",\n");
                writer.write("    \"bookTitle\": \"" + JsonUtility.escapeJson(record.getBookTitle()) + "\",\n");
                writer.write("    \"borrowDate\": \"" + record.getBorrowDate() + "\",\n");
                writer.write("    \"dueDate\": \"" + record.getDueDate() + "\",\n");
                writer.write("    \"returnDate\": \"" + (record.getReturnDate() != null ? record.getReturnDate() : "null") + "\"\n");
                writer.write("  }");
                if (i < borrowRecords.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write("]");
        } catch (IOException e) {
            System.out.println("Error saving borrow records: " + e.getMessage());
        }
    }

    // LOAD JSON (SIMPLIFIED)
    private void loadBorrowRecords() {
        ensureJsonFileExists();
        borrowRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line).append('\n');
            }

            Pattern objectPattern = Pattern.compile("\\{[^{}]*}");
            Matcher objectMatcher = objectPattern.matcher(json.toString());

            while (objectMatcher.find()) {
                String obj = objectMatcher.group();
                String memberId = JsonUtility.extractString(obj, "memberId");
                String memberName = JsonUtility.extractString(obj, "memberName");
                String bookTitle = JsonUtility.extractString(obj, "bookTitle");
                String borrowDateStr = JsonUtility.extractString(obj, "borrowDate");
                String dueDateStr = JsonUtility.extractString(obj, "dueDate");
                String returnDateStr = JsonUtility.extractString(obj, "returnDate");

                if (memberId == null || bookTitle == null || borrowDateStr == null || dueDateStr == null) {
                    continue;
                }

                if (memberName == null) memberName = "";

                try {
                    LocalDate borrowDate = LocalDate.parse(borrowDateStr);
                    LocalDate dueDate = LocalDate.parse(dueDateStr);
                    LocalDate returnDate = null;

                    if (returnDateStr != null && !returnDateStr.equals("null")) {
                        returnDate = LocalDate.parse(returnDateStr);
                    }

                    BorrowRecord record = new BorrowRecord(memberId, memberName, bookTitle, borrowDate, dueDate);
                    if (returnDate != null) {
                        record.setReturnDate(returnDate);
                    }
                    borrowRecords.add(record);
                } catch (Exception e) {
                    System.out.println("Error parsing borrow record: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading borrow records: " + e.getMessage());
        }
    }

    private void ensureJsonFileExists() {
        File jsonFile = new File(FILE_NAME);
        if (jsonFile.exists()) {
            return;
        }

        borrowRecords = new ArrayList<>();
        saveBorrowRecords();
    }


    // 🔹 INCREASE AVAILABLE COPIES OF A BOOK
    private void returnBookCopy(String bookTitle) {
        ArrayList<Book> bookList = bookManagement.getBookList();
        for (Book b : bookList) {
            if (b.getTitle().equalsIgnoreCase(bookTitle)) {
                b.returnBook(); // Use the Book class method
                return;
            }
        }
    }
}
