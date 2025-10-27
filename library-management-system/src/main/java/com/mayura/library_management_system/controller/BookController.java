package com.mayura.library_management_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayura.library_management_system.model.Author;
import com.mayura.library_management_system.model.Book;
import com.mayura.library_management_system.repository.AuthorRepository;
import com.mayura.library_management_system.repository.BookRepository;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {
	
	@Autowired
	BookRepository bookRepo;
	
	@Autowired
	AuthorRepository authorRepo;
	
	@GetMapping
	public List<Book> getAllBooks(){
		return bookRepo.findAll();
	}
	
	
	

	 @DeleteMapping("/{id}")
	 public void deleteCourse(@PathVariable Long id) {
		 bookRepo.deleteById(id);
	 }
	 
	 
//	 @PostMapping
//	 	public Book addBook(@RequestBody Book book) {
//		 	if(book.getAuthor() != null && book.getAuthor().getId() != null) {
//		 		Author author = authorRepo.findById(book.getAuthor().getId())
//                        .orElseThrow(() -> new RuntimeException("Author not found"));	
//		 		book.setAuthor(author);
//		 	}
//		 	return bookRepo.save(book);
//	 }
	 
	 @PostMapping("/book")
	 public Book addBook(@RequestBody Book book) {
	     if (book.getAuthor() != null) {
	         Author author = null;

	         // If author ID exists → fetch existing author
	         if (book.getAuthor().getId() != null) {
	             author = authorRepo.findById(book.getAuthor().getId())
	                     .orElseThrow(() -> new RuntimeException("Author not found"));
	         } 
	         // Else if author name provided → check by name or create new
	         else if (book.getAuthor().getName() != null) {
	             author = authorRepo.findByName(book.getAuthor().getName())
	                     .orElseGet(() -> authorRepo.save(book.getAuthor()));
	         }

	         book.setAuthor(author);
	     }

	     return bookRepo.save(book);
	 }


}
