package snackscription.review.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import snackscription.review.model.Review;
import snackscription.review.repository.ReviewRepository;
import snackscription.review.service.ReviewService;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public ResponseEntity<String> reviewPage() {
        return ResponseEntity.ok().body("Welcome to the review service!");
    }

    // @GetMapping("/api/subscription-boxes/{subsboxId}")
    // public ArrayList<Review> getAllSubscriptionBoxReview(@PathVariable String subsboxId) throws Exception {
    //     return reviewService.getAllSubscriptionBoxReview(subsboxId); 
    // }

    @GetMapping("/api/reviews/{reviewId}")
    public Review getById(@PathVariable String reviewId) throws Exception {
        return reviewService.findById(reviewId); 
    }

    @GetMapping("/api/subscription-boxes/{subscriptionBoxId}")
    public List<Review> getBySubscriptionBoxId(@PathVariable String subscriptionBoxId) throws Exception {
        return reviewService.findBySubscriptionBoxId(subscriptionBoxId);
    }

}