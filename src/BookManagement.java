import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BookManagement {

    private ArrayList<Book> bookList = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    private final String FILE_NAME = "books.json";
    private final Gson gson = new Gson();

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
                System.out.println("❌ Invalid input. Please enter a number.");
                scanner.nextLine(); 
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
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

        bookList.add(new Book(title, author, year, genre, isbn));
        saveBooks();
        System.out.println("Book added successfully!");
    } catch (Exception e) {
        System.out.println("❌ Error: Could not add book. Check your input.");
        scanner.nextLine(); // Clear buffer in case of error
    }
}

    // 🔹 UPDATE
    private void updateBook() {
        try {
            System.out.print("Select book number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= bookList.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }

            System.out.print("Enter new title: ");
            String newTitle = scanner.nextLine().trim();

            if (newTitle.isEmpty()) {
                System.out.println("❌ Title cannot be empty.");
                return;
            }

            bookList.get(index).setTitle(newTitle);
            saveBooks();
            System.out.println("Book updated.");

        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error updating member.");
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
                System.out.println("❌ Invalid selection.");
                return;
            }

            Book removed = bookList.remove(index);
            saveBooks();
            System.out.println("Deleted: " + removed);

        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error deleting member.");
        }
    }

    // 🔹 SEARCH
    private void searchBooks() {
        try {
            System.out.print("Enter title to search: ");
            String title = scanner.nextLine().trim();

            for (Book b : bookList) {
                if (b.getTitle().equalsIgnoreCase(title)) {
                    System.out.println("Found: " + b);
                    return;
                }
            }

            System.out.println("Book not found.");

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

    // 🔹 SAVE TO JSON
    private void saveBooks() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(bookList, writer);
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
        }
    }

    // 🔹 LOAD FROM JSON
    private void loadBooks() {
        try (Reader reader = new FileReader(FILE_NAME)) {

            Type type = new TypeToken<ArrayList<Book>>() {}.getType();
            bookList = gson.fromJson(reader, type);

            if (bookList == null) {
                bookList = new ArrayList<>();
            }

        } catch (FileNotFoundException e) {
            bookList = new ArrayList<>();
        } catch (Exception e) {
            System.out.println("❌ Error loading file.");
            bookList = new ArrayList<>();
        }
    }
}
