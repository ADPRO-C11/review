package snackscription.review.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




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

    @PostMapping("/api/subscription-boxes/{subscriptionBoxId}")
    public ResponseEntity<Review> createSubscriptionBoxReview(@RequestBody Map<String,String> body, @PathVariable String subscriptionBoxId) {
        
        try {
            String userId = body.get("userId");
            int rating = Integer.parseInt(body.get("rating"));
            String content = body.get("content");

            Review review = reviewService.createReview(rating, content, subscriptionBoxId, userId);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/subscription-boxes/{subscriptionBoxId}")
    public ResponseEntity<List<Review>> getAllPublicSubscriptionBoxReview(@PathVariable String subscriptionBoxId) {
        try {
            List<Review> reviews = reviewService.getAllSubscriptionBoxReview(subscriptionBoxId, "APPROVED");
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/subscription-boxes/{subscriptionBoxId}/users/self")
    public ResponseEntity<Review> getSelfSubscriptionBoxReview(@RequestBody Map<String,String> body, @PathVariable String subscriptionBoxId) {
        try {
            String userId = body.get("userId");
            Review review = reviewService.getReview(subscriptionBoxId, userId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/subscription-boxes/{subscriptionBoxId}/users/self")
    public ResponseEntity<Review> editSelfSubscriptionBoxId(@RequestBody Map<String,String> body, @PathVariable String subscriptionBoxId) {
        try {
            String userId = body.get("userId");
            int rating = Integer.parseInt(body.get("rating"));
            String content = body.get("content");

            Review review = reviewService.editReview(rating, content, subscriptionBoxId, userId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
   }

    @GetMapping("/api/reviews/{subsboxId}")
    public List<Review> getBySubscriptionBoxId(@PathVariable String subsboxId) throws Exception {
        return reviewService.getAllSubscriptionBoxReview(subsboxId, null); 
    }

    @GetMapping("/api/reviews/{reviewId}")
    public Review getById(@PathVariable String reviewId) throws Exception {
        return reviewService.findById(reviewId); 
    }

    @PutMapping("/api/reviews/{reviewId}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable String reviewId) {
        try {
            Review review = reviewService.approveReview(reviewId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/reviews/{reviewId}/reject")
    public ResponseEntity<Review> rejectReview(@PathVariable String reviewId) {
        try {
            Review review = reviewService.rejectReview(reviewId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}