package com.mayura.library_management_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.mayura.library_management_system.model.Rating;
import com.mayura.library_management_system.repository.RatingRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

public class RatingController {

	 private final RatingRepository ratingRepository;

	    public RatingController(RatingRepository ratingRepository) {
	        this.ratingRepository = ratingRepository;
	    }

	    @PostMapping
	    public Rating addRating(@RequestBody Rating rating) {
	        return ratingRepository.save(rating);
	    }

	    @GetMapping("/{itemId}")
	    public List<Rating> getRatings(@PathVariable Long itemId) {
	        return ratingRepository.findByItemId(itemId);
	    }
}
