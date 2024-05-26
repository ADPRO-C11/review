package snackscription.review.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;
import snackscription.review.service.ReviewServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("")
public class ReviewController {
    private ReviewService reviewService;

    public static final String BODY_AUTHOR = "author";
    public static final String BODY_CONTENT = "content";
    public static final String BODY_RATING = "rating";

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public ResponseEntity<String> reviewPage() {
        return ResponseEntity.ok().body("Welcome to the review service!");
    }

    @PostMapping("/subscription-boxes/{subsbox}/users/self")
    public ResponseEntity<Review> createSelfSubsboxReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
        try {
            String author = body.get(BODY_AUTHOR);
            int rating = Integer.parseInt(body.get(BODY_RATING));
            String content = body.get(BODY_CONTENT);

            Review review = reviewService.createReview(rating, content, subsbox, author);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}")
    public ResponseEntity<List<Review>> getPublicSubsboxReview(@PathVariable String subsbox) {
        try {
            List<Review> reviews = reviewService.getSubsboxReview(subsbox, "APPROVED");
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}/users/self")
    public ResponseEntity<Review> getSelfSubsboxReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
        try {
            String author = body.get("author"); // TODO: nanti pakai JWT token untuk ambil sendernya
            Review review = reviewService.getReview(subsbox, author);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/subscription-boxes/{subsbox}/users/self")
    public ResponseEntity<Review> editSelfReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
        try {
            String author = body.get("author"); // TODO: nanti pakai JWT token untuk ambil sendernya
            int rating = Integer.parseInt(body.get("rating"));
            String content = body.get("content");

            Review review = reviewService.editReview(rating, content, subsbox, author);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
   }

    @DeleteMapping("/subscription-boxes/{subsbox}/users/self")
    public ResponseEntity<Review> deleteSelfReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
        try {
            String author = body.get("author"); // TODO: nanti pakai JWT token untuk ambil sendernya
            reviewService.deleteReview(subsbox, author);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}