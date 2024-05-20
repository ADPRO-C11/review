package snackscription.review.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;

@RestController
@RequestMapping("")
public class ReviewController {
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public ResponseEntity<String> reviewPage() {
        return ResponseEntity.ok().body("Welcome to the review service!");
    }

    @PostMapping("/subscription-boxes/{subsbox}")
    public ResponseEntity<Review> createSubsboxReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
        try {
            String author = body.get("author");
            int rating = Integer.parseInt(body.get("rating"));
            String content = body.get("content");

            Review review = reviewService.createReview(rating, content, subsbox, author);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}/public")
    public ResponseEntity<List<Review>> getPublicSubsboxReview(@PathVariable String subsbox) {
        try {
            List<Review> reviews = reviewService.getSubsboxReview(subsbox, "APPROVED");
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}/users/{user}")
    public ResponseEntity<Review> getSelfSubsboxReview(@RequestBody Map<String,String> body, @PathVariable String subsbox, @PathVariable String user) {
        try {
            String sender = body.get("author"); // TODO: nanti pakai JWT token untuk ambil sendernya
            if (!authenticate(sender, user)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Review review = reviewService.getReview(subsbox, user);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/subscription-boxes/{subsbox}/users/{user}")
    public ResponseEntity<Review> editReview(@RequestBody Map<String,String> body, @PathVariable String subsbox, @PathVariable String user) {
        try {
            String sender = body.get("author"); // TODO: nanti pakai JWT token untuk ambil sendernya
            if (!authenticate(sender, user)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            int rating = Integer.parseInt(body.get("rating"));
            String content = body.get("content");

            Review review = reviewService.editReview(rating, content, subsbox, user);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
   }

    private boolean authenticate(String sender, String user) {
        return true;  
    }

    @DeleteMapping("/subscription-boxes/{subsbox}/users/{user}")
    public ResponseEntity<Review> deleteReview(@PathVariable String subsbox, @PathVariable String user) {
        try {
            reviewService.deleteReview(subsbox, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/subscription-boxes/{subsbox}")
    public List<Review> getSubsboxReview(@PathVariable String subsbox) throws Exception {
        return reviewService.getSubsboxReview(subsbox, null); 
    }
}