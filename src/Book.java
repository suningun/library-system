public class Book {
    private String title;
    private String author;
    private int year;
    private String genre;
    private String isbn;
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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public String toString() {
        return String.format("Title: %s | Author: %s | Year: %d | Genre: %s | ISBN: %s | Copies: %d/%d",
                              title, author, year, genre, isbn, availableCopies, totalCopies);
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

    // Add more copies to the book (increases both total and available copies)
    public void addCopies(int copiesToAdd) {
        if (copiesToAdd > 0) {
            this.totalCopies += copiesToAdd;
            this.availableCopies += copiesToAdd;
        }
    }

    // Return a book (increase available copies)
    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }
}
