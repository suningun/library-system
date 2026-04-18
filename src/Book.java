public class Book {
    private String title;
    private String author;
    private int year;
    private String genre;
    private String isbn;

    public Book(String title, String author, int year, String genre, String isbn) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.isbn = isbn;
    }

    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    
    @Override
    public String toString() {
        return String.format("Title: %s | Author: %s | Year: %d | Genre: %s | ISBN: %s", 
                              title, author, year, genre, isbn);
    }
}
