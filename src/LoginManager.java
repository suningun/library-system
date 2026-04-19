import java.util.Scanner;

public class LoginManager {
    private String correctUsername = "admin";
    private String correctPassword = "admin@168";
    private Scanner scanner;

    public LoginManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void login() {
        int attempts = 3;

        System.out.println("\n==============================");
        System.out.println("  LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==============================");

        while (attempts > 0) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (username.equals(correctUsername) && password.equals(correctPassword)) {
                System.out.println("Login Successful!");
                return;
            } else {
                attempts--;
                System.out.println("Invalid login. Attempts left: " + attempts);
            }
        }

        System.out.println("Too many failed attempts. System locked.");
        System.exit(0);
    }
}