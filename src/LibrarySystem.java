import java.util.Scanner;

public class LibrarySystem {
    private final Scanner scanner;
    private final LoginManager loginManager;
    private final BookManagement bookManagement;
    private final MemberManagement memberManagement;
    private final BorrowBook borrowBook;
    private final ReturnBook returnBook;

    public LibrarySystem() {
        scanner = new Scanner(System.in);
        loginManager = new LoginManager(scanner);
        bookManagement = new BookManagement(scanner);
        memberManagement = new MemberManagement(scanner);
        borrowBook = new BorrowBook(scanner, bookManagement, memberManagement);
        returnBook = new ReturnBook(scanner, bookManagement, memberManagement);
    }

    public void start() {
        loginManager.login();

        int choice;
        do {
            showMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    bookManagement.run();
                    break;
                case 2:
                    memberManagement.run();
                    break;
                case 3:
                    borrowBook.run();
                    break;
                case 4:
                    returnBook.run();
                    break;
                case 5:
                    returnBook.viewOverdueRecords();
                    break;
                case 6:
                    borrowBook.viewBorrowRecords();
                    break;
                case 7:
                    System.out.println("Logged out.");
                    loginManager.login();
                    break;
                case 8:
                    System.out.println("System closed.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 8);

        scanner.close();
    }

    private void showMenu() {
        System.out.println("\n===== DASHBOARD =====");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrow Book");
        System.out.println("4. Return Book");
        System.out.println("5. Overdue List");
        System.out.println("6. View Borrow Records");
        System.out.println("7. Log Out");
        System.out.println("8. Exit");
        System.out.print("Enter choice: ");
    }
}