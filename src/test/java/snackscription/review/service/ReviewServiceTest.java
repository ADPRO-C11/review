package snackscription.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

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


    @Test
    public void getReviewById() throws Exception {
        ReviewService reviewService = new ReviewService(reviewRepo); 

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
    
}
