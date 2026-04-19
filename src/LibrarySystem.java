import java.util.Scanner;

public class LibrarySystem {
    private final Scanner scanner;
    private final LoginManager loginManager;
    private final BookManagement bookManagement;
    private final MemberManagement memberManagement;

    public LibrarySystem() {
        scanner = new Scanner(System.in);
        loginManager = new LoginManager(scanner);
        bookManagement = new BookManagement(scanner);
        memberManagement = new MemberManagement(scanner);
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
                    System.out.println("Borrow Book (Coming soon)");
                    break;
                case 4:
                    System.out.println("Return Book (Coming soon)");
                    break;
                case 5:
                    System.out.println("Overdue List (Coming soon)");
                    break;
                case 6:
                    System.out.println("Borrow Records (Coming soon)");
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