package snackscription.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.text.html.Option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.ResultActions;

import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    
    @Mock
    ReviewRepository reviewRepo;

    ReviewService reviewService;

    List<Review> reviews;

    // @Test
    // public void testGetAllSubscriptionBoxReview() {
    //     ReviewService reviewService = new ReviewService(reviewRepo);

    //     Optional<Review> review = Optional.of(new Review(
    //         5, "amazing", "user1", "subsboxId"
    //     ));

    //     when(reviewRepo.findById("subsboxId")).thenReturn(review);

    //     Review foundReview = reviewService.getAllSubscriptionBoxReview("subsboxId");

    //     assertEquals(review.get(), foundReview);

    //     verify(reviewRepo).findBySubscriptionBoxId("subsboxId");
        
    // }

    @BeforeEach
    public void setUp() {
        reviewService = new ReviewService(reviewRepo);

        reviews = new ArrayList<>();
        reviews.add(new Review(5, "Bagus banget", "user_123", "subsbox_123"));
        reviews.add(new Review(1, "Jelek banget", "user_124", "subsbox_123"));
        reviews.add(new Review(2, "Lorem Ipsum", "user_124", "subsbox_124"));
    }

    @Test
    public void getReviewById() throws Exception {


        Optional<Review> review = Optional.of(new Review(
            5, "amazing", "user1", "subsboxId"
        ));

        String reviewId = review.get().getId();

        when(reviewRepo.findById(reviewId)).thenReturn(review);

        Review foundReview = reviewService.findById(reviewId);

        assertEquals(foundReview, review.get());

        verify(reviewRepo).findById(reviewId);
    }

    @Test
    public void getReviewByIdNotFound() {
        ReviewService reviewService = new ReviewService(reviewRepo);

        Optional<Review> review = Optional.empty();

        String reviewId = "reviewId";

        when(reviewRepo.findById(reviewId)).thenReturn(review);

        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.findById(reviewId);
        });

        verify(reviewRepo).findById(reviewId);
    }

    @Test
    public void getReviewsBySubscriptionBoxId() {
        ReviewService reviewService = new ReviewService(reviewRepo);

        List<Review> curReviews = new ArrayList<>();

        String subscriptionBoxId = this.reviews.getFirst().getSubscriptionBoxId();
        for (Review review : this.reviews) {
            if (review.getSubscriptionBoxId().equals(subscriptionBoxId)) {
                curReviews.add(review);
            }
        }

        when(reviewRepo.findBySubscriptionBoxId(subscriptionBoxId)).thenReturn(curReviews);

        List<Review> foundReviews = reviewService.findBySubscriptionBoxId(subscriptionBoxId);

        assertEquals(curReviews, foundReviews);

        verify(reviewRepo).findBySubscriptionBoxId(subscriptionBoxId);
    }

    
}
