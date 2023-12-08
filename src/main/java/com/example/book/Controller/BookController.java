package com.example.book.Controller;

import com.example.book.Model.Book;
import com.example.book.Model.Page;
import com.example.book.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Crear un nuevo libro
    @PostMapping("/create/{username}")
    public ResponseEntity<?> createBook(@PathVariable String username, @RequestBody Book book) {
        try {
            bookService.createBook(username, book);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{username}/all")
    public ResponseEntity<?> getUserBooks(@PathVariable String username) {
        try {
            return ResponseEntity.ok(bookService.getUserBooks(username));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{username}/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId, @PathVariable String username) {
        try {
            Book book = bookService.getBookById(bookId, username);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @GetMapping("/{bookId}/pages")
    public ResponseEntity<Set<Page>> getPagesByBook(@PathVariable Long bookId) {
        try {
            Set<Page> pages = bookService.getPagesByBook(bookId);
            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{bookId}/nextPage")
    public ResponseEntity<Page> nextPage(@PathVariable Long bookId) {
        try {
            Page Pages = bookService.moveToNextPage(bookId);
            return ResponseEntity.ok(Pages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{bookId}/createPage")
    public ResponseEntity<Page> createPage(@PathVariable Long bookId, @RequestBody Page page) {
        try {
            Page newPage = bookService.createPage(bookId, page);
            return ResponseEntity.ok(newPage);
        } catch (Exception e) {
            e.printStackTrace(); // Agrega un log para depuración
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Eliminar un libro
    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        try {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Eliminar una página
    @DeleteMapping("/{bookId}/deletePage/{pageId}")
    public ResponseEntity<Set<Page>> deletePage(@PathVariable Long bookId, @PathVariable Long pageId) {
        try {
            Set<Page> updatedPages = bookService.deletePage(bookId, pageId);
            return ResponseEntity.ok(updatedPages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(bookId, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{bookId}/updatePage/{pageId}")
    public ResponseEntity<Page> updatePage(@PathVariable Long bookId, @PathVariable Long pageId, @RequestBody Page pageDetails) {
        try {
            Page updatedPage = bookService.updatePage(bookId, pageId, pageDetails);
            return ResponseEntity.ok(updatedPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }




}
