package org.example.librarymanagementsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibraryManagementSystem {

}

class Library {
    String id;
    String name;
    String address;
    List<BookItem> bookItems;
}

enum Genre {
    FICTION, HISTORY, FINANCE
}

class Book {
    String isbn;
    String title;
    String author;
    Genre genre;
    LocalDate publishedAt;
}


enum BookItemStatus {
    AVAILABLE, RESERVED, BORROWED, LOST
}

class BookItem {
    String id;
    BookItemStatus status;
    LocalDate purchasedAt;
    Rack rack;

    Book book; // aggregation.
}

class Rack {
    int number;
    String locationIdentifier;
    List<BookItem> bookItems;
}

abstract class User {
    int id;
    String name;
    String password;

}

class Librarian extends User {

    public void addBook(Book book) {

    }

    public void removeBook(Book book) {

    }
}

class Member extends User {

    List<BookItem> borrowedBookItems;

    public void borrowBookItem(BookItem item) {
        borrowedBookItems.add(item);
    }

    /** .... */
}

enum ReservationStatus {
    WAITING, RESERVED, CANCEL;
}

class Reservation {
    String id;
    String bookItemId;
    String memberId;
    ReservationStatus status;
    LocalDateTime createdAt;
}

class Transaction {
    String id;
    String bookItemId;
    String memberId;
    LocalDateTime borrowAt;
    LocalDateTime dueAt;
    LocalDateTime returnAt;
    LocalDate createdAt;
}

interface Search {

    List<BookItem> searchByTitle(String title);

    List<BookItem> searchByAuthor(String author);
}

class Catalog implements Search {

    Map<String, List<BookItem>> titleBookItems;

    Map<String, List<BookItem>> authorBookItems;

    public void addTitleBookItems(String term, BookItem bookItem) {
        titleBookItems.computeIfAbsent(term, k -> new ArrayList<>()).add(bookItem);
    }

    public void addAuthorBookItems(String term, BookItem bookItem) {
        authorBookItems.computeIfAbsent(term, k -> new ArrayList<>()).add(bookItem);
    }

    @Override
    public List<BookItem> searchByTitle(String title) {
        return titleBookItems.getOrDefault(title, List.of());
    }

    @Override
    public List<BookItem> searchByAuthor(String author) {
        return authorBookItems.getOrDefault(author, List.of());
    }
}