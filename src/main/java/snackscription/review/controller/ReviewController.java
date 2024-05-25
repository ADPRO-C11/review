package snackscription.review.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;

@RestController
@RequestMapping("/reviews")
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

    @PostMapping("/subscription-boxes/{subsbox}")
    public ResponseEntity<Review> createSubsboxReview(@RequestBody Map<String,String> body, @PathVariable String subsbox) {
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
            String sender = body.get(BODY_AUTHOR); 
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
            String sender = body.get(BODY_AUTHOR);
            if (!authenticate(sender, user)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            int rating = Integer.parseInt(body.get(BODY_RATING));
            String content = body.get(BODY_CONTENT);

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
}