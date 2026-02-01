package com.mayura.library_management_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.mayura.library_management_system.model.Rating;
import com.mayura.library_management_system.repository.RatingRepository;

@RestController
@RequestMapping("/api/rating")
@CrossOrigin(origins = "http://localhost:5173")
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
    public List<Rating> getRatings(@PathVariable("itemId") Long itemId) {
        return ratingRepository.findByItemId(itemId);
    }
    
    @GetMapping
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
