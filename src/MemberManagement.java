import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberManagement {

    private ArrayList<Member> memberList = new ArrayList<>();
    private final Scanner scanner;

    private static final String FILE_NAME = "members.json";

    public MemberManagement(Scanner scanner) {
        this.scanner = scanner;
    }

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
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear buffer
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

     // 🔹 ADD
     private void addMember() {
         try {
             System.out.print("Enter name: ");
             String name = scanner.nextLine().trim();

             if (name.isEmpty()) {
                 System.out.println("Name cannot be empty.");
                 return;
             }

             String memberId = generateMemberId();
             memberList.add(new Member(memberId, name));
             saveMembers();
             System.out.println("Member added with ID: " + memberId);

         } catch (Exception e) {
             System.out.println("Error adding member.");
         }
     }

    // 🔹 UPDATE
    private void updateMember() {
        try {
            System.out.print("Select member number: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index < 0 || index >= memberList.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            System.out.print("Enter new name: ");
            String newName = scanner.nextLine().trim();

            if (newName.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }

            memberList.get(index).setName(newName);
            saveMembers();
            System.out.println("Member updated.");

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
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
                System.out.println("Invalid selection.");
                return;
            }

            Member removed = memberList.remove(index);
            saveMembers();
            System.out.println("Deleted: " + removed);

        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
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

     // SAVE JSON
     private void saveMembers() {
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
             writer.write("[\n");
             for (int i = 0; i < memberList.size(); i++) {
                 Member member = memberList.get(i);
                 writer.write("  {\n");
                 writer.write("    \"id\": \"" + escapeJson(member.getId()) + "\",\n");
                 writer.write("    \"name\": \"" + escapeJson(member.getName()) + "\"\n");
                 writer.write("  }");
                 if (i < memberList.size() - 1) {
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
     private void loadMembers() {
         ensureJsonFileExists();
         memberList = new ArrayList<>();
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
                 String id = extractString(obj, "id");
                 String name = extractString(obj, "name");
                 if (name == null || name.trim().isEmpty()) {
                     continue;
                 }
                 if (id == null) {
                     id = generateMemberId();
                 }
                 memberList.add(new Member(id, name));
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

        memberList = new ArrayList<>();
        saveMembers();
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

    private String generateMemberId() {
        int nextId = memberList.size() + 1;
        return String.format("%03d", nextId);
    }

    // 🔹 PUBLIC METHOD TO LOAD MEMBERS (for external use)
    public void loadMembersData() {
        loadMembers();
    }
}