public class Book {
    private String title;
    private final String author;
    private final int year;
    private final String genre;
    private final String isbn;
    private int totalCopies;
    private int availableCopies;

    public Book(String title, String author, int year, String genre, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies; // Initially all copies are available
    }

    // Legacy constructor for backward compatibility
    public Book(String title, String author, int year, String genre, String isbn) {
        this(title, author, year, genre, isbn, 1); // Default to 1 copy
    }

    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    
    @Override
    public String toString() {
        return String.format("Title: %s | Author: %s | Year: %d | Genre: %s | ISBN: %s | Copies: %d/%d",
                              title, author, year, genre, isbn, availableCopies, totalCopies);
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // Borrow a book (decrease available copies)
    public boolean borrowBook() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    // Return a book (increase available copies)
    public boolean returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            return true;
        }
        return false;
    }
}
