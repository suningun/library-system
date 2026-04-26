import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BorrowBook {

    private ArrayList<BorrowRecord> borrowRecords = new ArrayList<>();
    private final Scanner scanner;
    private final BookManagement bookManagement;
    private final MemberManagement memberManagement;

    private static final String FILE_NAME = "borrowRecord.json";
    private static final int BORROW_DAYS = 3; // Books can be borrowed for 3 days

    public BorrowBook(Scanner scanner, BookManagement bookManagement, MemberManagement memberManagement) {
        this.scanner = scanner;
        this.bookManagement = bookManagement;
        this.memberManagement = memberManagement;
    }

    // 🔹 MAIN RUN METHOD
    public void run() {
        loadBorrowRecords();

        // Ensure member and book data are loaded
        memberManagement.loadMembersData();
        bookManagement.loadBooksData();

        try {
            System.out.println("\n--- Borrow Book ---");
            System.out.print("Enter Member ID: ");
            String memberId = scanner.nextLine().trim();

            // Check if member exists and get member name
            String memberName = getMemberName(memberId);
            if (memberName == null) {
                System.out.println("Member not found. Cannot proceed with borrow.");
                return;
            }

            System.out.print("Enter Book Title: ");
            String bookTitle = scanner.nextLine().trim();

            // Check if book exists
            if (!bookExists(bookTitle)) {
                System.out.println("Book not found. Cannot proceed with borrow.");
                return;
            }

            // Check if book has available copies
            if (!hasAvailableCopies(bookTitle)) {
                System.out.println("No copies of this book are currently available.");
                return;
            }

            // Check if book is already borrowed by this member
            if (isBookAlreadyBorrowed(memberId, bookTitle)) {
                System.out.println("This member has already borrowed this book.");
                return;
            }

            // Create borrow record
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(BORROW_DAYS);
            
            BorrowRecord record = new BorrowRecord(memberId, memberName, bookTitle, borrowDate, dueDate);
            borrowRecords.add(record);
            
            // Decrease available copies
            borrowBookCopy(bookTitle);
            
            saveBorrowRecords();

            // Save updated book stock to JSON
            bookManagement.saveBooksData();

            System.out.println("\n✓ Book borrowed successfully!");
            System.out.println("Member: [" + memberId + "] " + memberName);
            System.out.println("Book Title: " + bookTitle);
            System.out.println("Borrow Date: " + borrowDate);
            System.out.println("Due Date: " + dueDate);

        } catch (Exception e) {
            System.out.println("Error during borrow process: " + e.getMessage());
        }
    }

    // 🔹 CHECK IF MEMBER EXISTS (SIMPLIFIED)
    private boolean memberExists(String memberId) {
        ArrayList<Member> memberList = memberManagement.getMemberList();
        for (Member m : memberList) {
            if (m.getId().equalsIgnoreCase(memberId)) {
                return true;
            }
        }
        return false;
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

    // 🔹 CHECK IF BOOK EXISTS (SIMPLIFIED)
    private boolean bookExists(String bookTitle) {
        ArrayList<Book> bookList = bookManagement.getBookList();
        for (Book b : bookList) {
            if (b.getTitle().equalsIgnoreCase(bookTitle)) {
                return true;
            }
        }
        return false;
    }

    // 🔹 CHECK IF BOOK HAS AVAILABLE COPIES (SIMPLIFIED)
    private boolean hasAvailableCopies(String bookTitle) {
        ArrayList<Book> bookList = bookManagement.getBookList();
        for (Book b : bookList) {
            if (b.getTitle().equalsIgnoreCase(bookTitle)) {
                return b.getAvailableCopies() > 0;
            }
        }
        return false;
    }

    // 🔹 DECREASE AVAILABLE COPIES OF A BOOK (SIMPLIFIED)
    private void borrowBookCopy(String bookTitle) {
        ArrayList<Book> bookList = bookManagement.getBookList();
        for (Book b : bookList) {
            if (b.getTitle().equalsIgnoreCase(bookTitle)) {
                b.setAvailableCopies(b.getAvailableCopies() - 1);
                return;
            }
        }
    }

    // 🔹 CHECK IF BOOK IS ALREADY BORROWED BY THIS MEMBER
    private boolean isBookAlreadyBorrowed(String memberId, String bookTitle) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getMemberId().equalsIgnoreCase(memberId) &&
                record.getBookTitle().equalsIgnoreCase(bookTitle) &&
                record.getReturnDate() == null) {
                return true;
            }
        }
        return false;
    }

    // 🔹 VIEW ALL ACTIVE BORROW RECORDS
    public void viewBorrowRecords() {
        loadBorrowRecords();

        // Ensure member and book data are loaded
        memberManagement.loadMembersData();
        bookManagement.loadBooksData();

        System.out.println("\n===== Active Borrow Records =====");

        ArrayList<BorrowRecord> activeRecords = new ArrayList<>();
        for (BorrowRecord record : borrowRecords) {
            if (record.getReturnDate() == null) {
                activeRecords.add(record);
            }
        }

        if (activeRecords.isEmpty()) {
            System.out.println("No active borrow records.");
            return;
        }

        for (int i = 0; i < activeRecords.size(); i++) {
            System.out.println((i + 1) + ". " + activeRecords.get(i));
        }
    }

    // SAVE JSON
    private void saveBorrowRecords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("[\n");
            for (int i = 0; i < borrowRecords.size(); i++) {
                BorrowRecord record = borrowRecords.get(i);
                writer.write("  {\n");
                writer.write("    \"memberId\": \"" + escapeJson(record.getMemberId()) + "\",\n");
                writer.write("    \"memberName\": \"" + escapeJson(record.getMemberName()) + "\",\n");
                writer.write("    \"bookTitle\": \"" + escapeJson(record.getBookTitle()) + "\",\n");
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

    // LOAD JSON
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
                String memberId = extractString(obj, "memberId");
                String memberName = extractString(obj, "memberName");
                String bookTitle = extractString(obj, "bookTitle");
                String borrowDateStr = extractString(obj, "borrowDate");
                String dueDateStr = extractString(obj, "dueDate");
                String returnDateStr = extractString(obj, "returnDate");

                if (memberId == null || bookTitle == null || borrowDateStr == null || dueDateStr == null) {
                    continue;
                }

                // If memberName is null (from old records), use empty string
                if (memberName == null) {
                    memberName = "";
                }

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

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String value) {
        return value.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    private String extractString(String obj, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher m = p.matcher(obj);
        if (!m.find()) {
            return null;
        }
        return unescapeJson(m.group(1));
    }
}
