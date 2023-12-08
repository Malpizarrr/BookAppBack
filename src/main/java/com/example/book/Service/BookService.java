package com.example.book.Service;

import com.example.book.Model.Book;
import com.example.book.Model.Page;
import com.example.book.Model.User;
import com.example.book.Repositories.BookRepository;
import com.example.book.Repositories.UserRepository;
import com.example.book.Repositories.PageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    public Book createBook(String username, Book book) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        book.setUser(user);
        return bookRepository.save(book);
    }

    public List<Book> getUserBooks(String username) {
        return bookRepository.findAllByUser_Username(username);
    }

    public Set<Page> getPagesByBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            return book.getPages();
        } else {
            throw new RuntimeException("Libro no encontrado");
        }
    }

    public Page moveToNextPage(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            throw new RuntimeException("Libro no encontrado");
        }

        Book book = optionalBook.get();
        Set<Page> pages = book.getPages();
        int currentPageNumber = book.getCurrentPage();

        Optional<Page> currentPage = pages.stream()
                .filter(page -> page.getPageNumber().equals(currentPageNumber))
                .findFirst();

        if (currentPage.isPresent()) {
            // If current page is not the last page, return the next page
            int nextPageNumber = currentPageNumber + 1;
            return pages.stream()
                    .filter(page -> page.getPageNumber().equals(nextPageNumber))
                    .findFirst()
                    .orElseGet(() -> createAndSaveNewPage(book, nextPageNumber));
        } else {
            // If current page does not exist, start from the first page
            return createAndSaveNewPage(book, 1);
        }
    }

    private Page createAndSaveNewPage(Book book, int pageNumber) {
        Page newPage = new Page();
        newPage.setBook(book);
        newPage.setPageNumber(pageNumber);
        newPage.setContent(""); // Set default content or use a parameter
        newPage.setCreatedAt(new Date()); // Set creation date
        // Optionally set updated date if required

        book.getPages().add(newPage);
        book.setCurrentPage(pageNumber);
        bookRepository.save(book);
        return newPage;
    }


    @Transactional
    public Page createPage(Long bookId, Page page) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        page.setBook(book);
        page.setCreatedAt(new Date());
        pageRepository.save(page);

        return page;
    }

    public Set<Page> deletePage(Long bookId, Long pageId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();

            Page pageToDelete = book.getPages().stream()
                    .filter(page -> page.getPageId().equals(pageId))
                    .findFirst()
                    .orElse(null);

            if (pageToDelete != null) {
                book.getPages().remove(pageToDelete);

                pageRepository.delete(pageToDelete);

                return book.getPages();
            } else {
                throw new RuntimeException("Página no encontrada en el libro");
            }
        } else {
            throw new RuntimeException("Libro no encontrado");
        }
    }

    public Book updateBook(Long bookId, Book bookDetails) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Update book details
        book.setTitle(bookDetails.getTitle());
        // more fields to update

        return bookRepository.save(book);
    }

    public Page updatePage(Long bookId, Long pageId, Page pageDetails) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        Page pageToUpdate = book.getPages().stream()
                .filter(p -> p.getPageId().equals(pageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Página no encontrada"));

        pageToUpdate.setContent(pageDetails.getContent());
        // Otros campos a actualizar si es necesario

        return pageRepository.save(pageToUpdate);
    }


    public Book getBookById(Long bookId, String username) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getUser().getUsername().equals(username)) {
                return book;
            } else {
                throw new RuntimeException("No tienes permiso para ver este libro");
            }
        } else {
            throw new RuntimeException("Libro no encontrado");
        }
    }

    public void deleteBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            bookRepository.delete(book);
        } else {
            throw new RuntimeException("Libro no encontrado");
        }
    }


}

