package snackscription.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import snackscription.review.model.Review;
import snackscription.review.repository.ReviewRepository;

@RestController
@RequestMapping("/")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("")
    public ResponseEntity<String> reviewPage() {
        return ResponseEntity.ok().body("Welcome to the review service!");
    }

    @GetMapping("/all")
    public Iterable<Review> findAllReview() {
        return this.reviewRepository.findAll();
    }
}