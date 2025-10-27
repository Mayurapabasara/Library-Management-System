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

@RestController
@RequestMapping("/api/author")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthorController {
	
	@Autowired
	AuthorRepository authorRepo;
	
	
	@GetMapping
	public List<Author> getAllAuthor() {
	    return authorRepo.findAll();
	}
	
	
	 @DeleteMapping("/{id}")
	 public void deleteAuthor(@PathVariable Long id) {
		 authorRepo.deleteById(id);
	 }

	 @PostMapping
	    public Author addAuthor(@RequestBody Author author) {
	        return authorRepo.save(author);
	    }

}
