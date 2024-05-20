package snackscription.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ReviewAdminController {
    private ReviewService reviewService;

    public ReviewAdminController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PutMapping("/subscription-boxes/{subsbox}/users/{user}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable String subsbox, @PathVariable String user) {
        try {
            Review review = reviewService.approveReview(subsbox, user);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/subscription-boxes/{subsbox}/users/{user}/reject")
    public ResponseEntity<Review> rejectReview(@PathVariable String subsbox, @PathVariable String user) {
        try {
            Review review = reviewService.rejectReview(subsbox, user);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}/reviews")
    public ResponseEntity<List<Review>> getSubsboxReviews(
            @PathVariable String subsbox,
            @RequestParam(required = false) String state) throws Exception {
        List<Review> reviews = reviewService.getSubsboxReview(subsbox, state);
        return ResponseEntity.ok().body(reviews);
    }

}
