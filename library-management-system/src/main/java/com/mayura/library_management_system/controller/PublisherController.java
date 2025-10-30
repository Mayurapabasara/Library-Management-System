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

import com.mayura.library_management_system.model.Publisher;
import com.mayura.library_management_system.repository.PublisherRepository;

@RestController
@RequestMapping("/api/publisher")
@CrossOrigin(origins = "http://localhost:5173")
public class PublisherController {
	
	@Autowired
	private PublisherRepository publisherRepo;
	
	// Get all publishers
	@GetMapping
	public List<Publisher> getAllPublisher() {
	    return publisherRepo.findAll();
	}
	
	// Delete a publisher by ID
	@DeleteMapping("/{id}")
	public void deletePublisher(@PathVariable Long id) {
	    publisherRepo.deleteById(id);
	}

	// Add a new publisher
	@PostMapping
	public Publisher addPublisher(@RequestBody Publisher publisher) {
	    return publisherRepo.save(publisher);
	}
}
