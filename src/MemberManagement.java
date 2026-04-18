import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MemberManagement {

    private ArrayList<Member> memberList = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    private final String FILE_NAME = "members.json";
    private final Gson gson = new Gson();

    // 🔹 MAIN RUN METHOD
    public void run() {
        loadMembers();

        while (true) {
            try {
                System.out.println("\n--- Member Management ---");
                System.out.println("1. Add Member");
                System.out.println("2. Update Member");
                System.out.println("3. Delete Member");
                System.out.println("4. Search Member");
                System.out.println("5. View All Members");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                int action = scanner.nextInt();
                scanner.nextLine();

                switch (action) {
                    case 1 -> addMember();
                    case 2 -> updateMember();
                    case 3 -> deleteMember();
                    case 4 -> searchMember();
                    case 5 -> viewMembers();
                    case 6 -> {
                        saveMembers();
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }

            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                scanner.nextLine(); // clear buffer
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }
    }

    // 🔹 ADD
    private void addMember() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty.");
                return;
            }

            memberList.add(new Member(name));
            saveMembers();
            System.out.println("Member added.");

        } catch (Exception e) {
            System.out.println("Error adding member.");
        }
    }

    // 🔹 UPDATE
    private void updateMember() {
        try {
            viewMembers();
            if (memberList.isEmpty()) return;

            System.out.print("Select member number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= memberList.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }

            System.out.print("Enter new name: ");
            String newName = scanner.nextLine().trim();

            if (newName.isEmpty()) {
                System.out.println("❌ Name cannot be empty.");
                return;
            }

            memberList.get(index).setName(newName);
            saveMembers();
            System.out.println("Member updated.");

        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error updating member.");
        }
    }

    // 🔹 DELETE (FIXED)
    private void deleteMember() {
        try {
            viewMembers();
            if (memberList.isEmpty()) return;

            System.out.print("Select member number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= memberList.size()) {
                System.out.println("❌ Invalid selection.");
                return;
            }

            Member removed = memberList.remove(index);
            saveMembers();
            System.out.println("Deleted: " + removed);

        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error deleting member.");
        }
    }

    // 🔹 SEARCH
    private void searchMember() {
        try {
            System.out.print("Enter name to search: ");
            String name = scanner.nextLine().trim();

            for (Member m : memberList) {
                if (m.getName().equalsIgnoreCase(name)) {
                    System.out.println("Found: " + m);
                    return;
                }
            }

            System.out.println("Member not found.");

        } catch (Exception e) {
            System.out.println("Error searching member.");
        }
    }

    // 🔹 VIEW
    private void viewMembers() {
        System.out.println("\n===== Member List =====");

        if (memberList.isEmpty()) {
            System.out.println("No members available.");
            return;
        }

        for (int i = 0; i < memberList.size(); i++) {
            System.out.println((i + 1) + ". " + memberList.get(i));
        }
    }

    // 🔹 SAVE TO JSON
    private void saveMembers() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(memberList, writer);
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
        }
    }

    // 🔹 LOAD FROM JSON
    private void loadMembers() {
        try (Reader reader = new FileReader(FILE_NAME)) {

            Type type = new TypeToken<ArrayList<Member>>() {}.getType();
            memberList = gson.fromJson(reader, type);

            if (memberList == null) {
                memberList = new ArrayList<>();
            }

        } catch (FileNotFoundException e) {
            memberList = new ArrayList<>();
        } catch (Exception e) {
            System.out.println("❌ Error loading file.");
            memberList = new ArrayList<>();
        }
    }
}