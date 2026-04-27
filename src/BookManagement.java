import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookManagement {

    private ArrayList<Book> bookList = new ArrayList<>();
    private final Scanner scanner;

    private static final String FILE_NAME = "books.json";

    public BookManagement(Scanner scanner) {
        this.scanner = scanner;
    }

    // 🔹 MAIN RUN METHOD
    public void run() {
        loadBooks();

        while (true) {
            try {
                System.out.println("\n--- Book Management ---");
                System.out.println("1. Add Book");
                System.out.println("2. Update Book");
                System.out.println("3. Delete Book");
                System.out.println("4. Search Book");
                System.out.println("5. View All Books");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                int action = scanner.nextInt();
                scanner.nextLine();

                switch (action) {
                    case 1 -> addBook();
                    case 2 -> updateBook();
                    case 3 -> deleteBook();
                    case 4 -> searchBooks();
                    case 5 -> viewBooks();
                    case 6 -> {
                        saveBooks();
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    // 🔹 ADD
    private void addBook() {
    try {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Total Copies: ");
        int totalCopies = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        if (totalCopies <= 0) {
            System.out.println("Total copies must be greater than 0.");
            return;
        }

        bookList.add(new Book(title, author, year, genre, isbn, totalCopies));
        saveBooks();
        System.out.println("Book added successfully!");
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter valid numbers for year and total copies.");
        scanner.nextLine(); // Clear buffer in case of error
    } catch (Exception e) {
        System.out.println("Error: Could not add book. Check your input.");
        scanner.nextLine(); // Clear buffer in case of error
    }
}

    // 🔹 UPDATE
    private void updateBook() {
        try {
            viewBooks();
            if (bookList.isEmpty()) return;

            System.out.print("Select book number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= bookList.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Book book = bookList.get(index);
            boolean updated = false;

            while (true) {
                System.out.println("\n--- Update Book ---");
                System.out.println("1. Update Title (Current: " + book.getTitle() + ")");
                System.out.println("2. Update Author (Current: " + book.getAuthor() + ")");
                System.out.println("3. Update Year (Current: " + book.getYear() + ")");
                System.out.println("4. Update Genre (Current: " + book.getGenre() + ")");
                System.out.println("5. Update ISBN (Current: " + book.getIsbn() + ")");
                System.out.println("6. Done");
                System.out.print("Select field to update: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter new title: ");
                        String newTitle = scanner.nextLine().trim();
                        if (newTitle.isEmpty()) {
                            System.out.println("Title cannot be empty.");
                        } else {
                            book.setTitle(newTitle);
                            System.out.println("Title updated.");
                            updated = true;
                        }
                    }
                    case 2 -> {
                        System.out.print("Enter new author: ");
                        String newAuthor = scanner.nextLine().trim();
                        if (newAuthor.isEmpty()) {
                            System.out.println("Author cannot be empty.");
                        } else {
                            book.setAuthor(newAuthor);
                            System.out.println("Author updated.");
                            updated = true;
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter new year: ");
                        try {
                            int newYear = scanner.nextInt();
                            scanner.nextLine();
                            book.setYear(newYear);
                            System.out.println("Year updated.");
                            updated = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid year. Please enter a valid number.");
                            scanner.nextLine();
                        }
                    }
                    case 4 -> {
                        System.out.print("Enter new genre: ");
                        String newGenre = scanner.nextLine().trim();
                        if (newGenre.isEmpty()) {
                            System.out.println("Genre cannot be empty.");
                        } else {
                            book.setGenre(newGenre);
                            System.out.println("Genre updated.");
                            updated = true;
                        }
                    }
                    case 5 -> {
                        System.out.print("Enter new ISBN: ");
                        String newIsbn = scanner.nextLine().trim();
                        if (newIsbn.isEmpty()) {
                            System.out.println("ISBN cannot be empty.");
                        } else {
                            book.setIsbn(newIsbn);
                            System.out.println("ISBN updated.");
                            updated = true;
                        }
                    }
                    case 6 -> {
                        if (updated) {
                            saveBooks();
                            System.out.println("Book updated successfully.");
                        } else {
                            System.out.println("No changes were made.");
                        }
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error updating book.");
        }
    }

    // 🔹 DELETE (FIXED)
    private void deleteBook() {
        try {
            viewBooks();
            if (bookList.isEmpty()) return;

            System.out.print("Select book number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= bookList.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Book removed = bookList.remove(index);
            saveBooks();
            System.out.println("Deleted: " + removed);

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error deleting book.");
        }
    }

     // 🔹 SEARCH
     private void searchBooks() {
         try {
             System.out.println("\n--- Search Book ---");
             System.out.println("1. Search by Title");
             System.out.println("2. Search by Author");
             System.out.println("3. Search by Year");
             System.out.println("4. Search by Genre");
             System.out.println("5. Search by ISBN");
             System.out.print("Enter choice: ");

             int searchChoice = scanner.nextInt();
             scanner.nextLine();

             ArrayList<Book> results = new ArrayList<>();

             switch (searchChoice) {
                 case 1 -> {
                     System.out.print("Enter title to search: ");
                     String title = scanner.nextLine().trim();
                     for (Book b : bookList) {
                         if (b.getTitle().equalsIgnoreCase(title)) {
                             results.add(b);
                         }
                     }
                 }
                 case 2 -> {
                     System.out.print("Enter author to search: ");
                     String author = scanner.nextLine().trim();
                     for (Book b : bookList) {
                         if (b.getAuthor().equalsIgnoreCase(author)) {
                             results.add(b);
                         }
                     }
                 }
                 case 3 -> {
                     System.out.print("Enter year to search: ");
                     int year = scanner.nextInt();
                     scanner.nextLine();
                     for (Book b : bookList) {
                         if (b.getYear() == year) {
                             results.add(b);
                         }
                     }
                 }
                 case 4 -> {
                     System.out.print("Enter genre to search: ");
                     String genre = scanner.nextLine().trim();
                     for (Book b : bookList) {
                         if (b.getGenre().equalsIgnoreCase(genre)) {
                             results.add(b);
                         }
                     }
                 }
                 case 5 -> {
                     System.out.print("Enter ISBN to search: ");
                     String isbn = scanner.nextLine().trim();
                     for (Book b : bookList) {
                         if (b.getIsbn().equalsIgnoreCase(isbn)) {
                             results.add(b);
                         }
                     }
                 }
                 default -> System.out.println("Invalid choice.");
             }

             if (results.isEmpty()) {
                 System.out.println("No books found.");
             } else {
                 System.out.println("\n===== Search Results =====");
                 for (int i = 0; i < results.size(); i++) {
                     System.out.println((i + 1) + ". " + results.get(i));
                 }
             }

         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter valid data.");
             scanner.nextLine();
         } catch (Exception e) {
             System.out.println("Error searching books.");
         }
     }

    // 🔹 VIEW
    private void viewBooks() {
        System.out.println("\n===== Book List =====");

        if (bookList.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        for (int i = 0; i < bookList.size(); i++) {
            System.out.println((i + 1) + ". " + bookList.get(i));
        }
    }

    // SAVE JSON
    private void saveBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("[\n");
            for (int i = 0; i < bookList.size(); i++) {
                Book book = bookList.get(i);
                writer.write("  {\n");
                writer.write("    \"title\": \"" + escapeJson(book.getTitle()) + "\",\n");
                writer.write("    \"author\": \"" + escapeJson(book.getAuthor()) + "\",\n");
                writer.write("    \"year\": " + book.getYear() + ",\n");
                writer.write("    \"genre\": \"" + escapeJson(book.getGenre()) + "\",\n");
                writer.write("    \"isbn\": \"" + escapeJson(book.getIsbn()) + "\",\n");
                writer.write("    \"totalCopies\": " + book.getTotalCopies() + ",\n");
                writer.write("    \"availableCopies\": " + book.getAvailableCopies() + "\n");
                writer.write("  }");
                if (i < bookList.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write("]");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // LOAD JSON
    private void loadBooks() {
        ensureJsonFileExists();
        bookList = new ArrayList<>();
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
                String title = extractString(obj, "title");
                String author = extractString(obj, "author");
                Integer year = extractInt(obj, "year");
                String genre = extractString(obj, "genre");
                String isbn = extractString(obj, "isbn");
                Integer totalCopies = extractInt(obj, "totalCopies");
                Integer availableCopies = extractInt(obj, "availableCopies");

                if (title == null || author == null || year == null || genre == null || isbn == null) {
                    continue;
                }

                // Backward compatibility: if totalCopies is not in JSON, default to 1
                if (totalCopies == null) {
                    totalCopies = 1;
                }

                Book book;
                if (availableCopies != null) {
                    // New format with stock tracking
                    book = new Book(title, author, year, genre, isbn, totalCopies);
                    book.setAvailableCopies(availableCopies);
                } else {
                    // Legacy format without stock tracking
                    book = new Book(title, author, year, genre, isbn, totalCopies);
                }

                bookList.add(book);
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private void ensureJsonFileExists() {
        File jsonFile = new File(FILE_NAME);
        if (jsonFile.exists()) {
            return;
        }

        bookList = new ArrayList<>();
        saveBooks();
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

    private Integer extractInt(String obj, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(obj);
        if (!m.find()) {
            return null;
        }
        try {
            return Integer.parseInt(m.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 🔹 PUBLIC METHOD TO LOAD BOOKS (for external use)
    public void loadBooksData() {
        loadBooks();
    }

    // 🔹 PUBLIC METHOD TO SAVE BOOKS (for external use)
    public void saveBooksData() {
        saveBooks();
    }

    // 🔹 PUBLIC GETTER FOR BOOK LIST
    public ArrayList<Book> getBookList() {
        return bookList;
    }
}
