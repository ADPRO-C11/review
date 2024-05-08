package snackscription.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.text.html.Option;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.ResultActions;

import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;
import snackscription.review.repository.ReviewRepository;
import snackscription.review.service.SentimentAnalysisService;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    SentimentAnalysisService sentimentAnalysisService;
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
        sentimentAnalysisService = new SentimentAnalysisService();

        Review review1 = new Review(5, "I love it", "user_123", "subsbox_123");
        Review review2 = new Review(1, "I hate it", "user_124", "subsbox_123");
        Review review3 = new Review(2, "Hmmmm idk", "user_124", "subsbox_124");
        Review review4 = new Review(3, "It's okay", "user_125", "subsbox_124");
        Review review5 = new Review(4, "I like it", "user_126", "subsbox_124");

        review1.setState(ReviewState.PENDING);
        review4.setState(ReviewState.APPROVED);
        review5.setState(ReviewState.REJECTED);

        reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);
        reviews.add(review3);
        reviews.add(review4);
        reviews.add(review5);
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

    @Test
    public void testCreateReview() throws Exception {
        Review review = reviews.getFirst();
        
        when(reviewRepo.save(any(Review.class))).thenReturn(review);

        Review savedReview = reviewService.createReview(
                review.getRating(),
                review.getContent(),
                review.getSubscriptionBoxId(),
                review.getUserId());

        assertEqualReview(review, savedReview);

        verify(reviewRepo).save(any(Review.class));
    }

    @Test
    public void testGetAllSubscriptionBoxReview() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubscriptionBoxId();

        List<Review> curReviews = new ArrayList<>();

        for (Review review : this.reviews) {
            if (review.getSubscriptionBoxId().equals(subscriptionBoxId)) {
                curReviews.add(review);
            }
        }

        when(reviewRepo.findBySubscriptionBoxId(subscriptionBoxId)).thenReturn(curReviews);

        List<Review> foundReviews = reviewService.getAllSubscriptionBoxReview(subscriptionBoxId, null);

        assertEquals(curReviews, foundReviews);

        verify(reviewRepo).findBySubscriptionBoxId(subscriptionBoxId);
    }

    @Test
    public void testGetAllSubscriptionBoxReviewApproved() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubscriptionBoxId();

        List <Review> cuReviews = new ArrayList<>();

        for (Review review : this.reviews) {
            if (review.getSubscriptionBoxId().equals(subscriptionBoxId) && review.getState().equals(ReviewState.APPROVED)) {
                cuReviews.add(review);
            }
        }

        when(reviewRepo.findBySubscriptionBoxIdAndState(subscriptionBoxId, ReviewState.APPROVED)).thenReturn(cuReviews);

        List<Review> foundReviews = reviewService.getAllSubscriptionBoxReview(subscriptionBoxId, "APPROVED");

        assertEquals(cuReviews, foundReviews);

        verify(reviewRepo).findBySubscriptionBoxIdAndState(subscriptionBoxId, ReviewState.APPROVED);
    }

    @Test
    public void testEditReview() throws Exception {
        Review review = reviews.getFirst();
        String subscriptionBoxId = review.getSubscriptionBoxId();
        String userId = review.getUserId();
        
        int newRating = 1;
        String newContent = "Changed content";
        Review newReview = new Review(newRating, newContent, userId, subscriptionBoxId);
        newReview.setId(review.getId());

        when(reviewRepo.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId)).thenReturn(review);
        when(reviewRepo.save(any(Review.class))).thenReturn(newReview);
        
        Review editedReview = reviewService.editReview(newRating, newContent, subscriptionBoxId, userId);

        assertEquals(newRating, editedReview.getRating());
        assertEquals(newContent, editedReview.getContent());
        assertEquals(subscriptionBoxId, editedReview.getSubscriptionBoxId());
        assertEquals(userId, editedReview.getUserId());
        assertEquals(review.getId(), editedReview.getId());
    }

    @Test
    public void testDeleteReview() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubscriptionBoxId();
        String userId = this.reviews.getFirst().getUserId();

        Review review = reviews.getFirst();

        when(reviewRepo.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId)).thenReturn(review);

        reviewService.deleteReview(subscriptionBoxId, userId);

        when(reviewRepo.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId)).thenReturn(null);

        assertNull(reviewService.getReview(subscriptionBoxId, userId));

        verify(reviewRepo).delete(review);
    }

    public void assertEqualReview(Review review1, Review review2) {
        assertEquals(review1.getRating(), review2.getRating());
        assertEquals(review1.getContent(), review2.getContent());
        assertEquals(review1.getUserId(), review2.getUserId());
        assertEquals(review1.getSubscriptionBoxId(), review2.getSubscriptionBoxId());
    }

    @Test
    public void testAnalyzeSentimentAsyncTest() {
        String reviewText = "This is a great product!";
        String expectedSentiment = "positive";

        when(sentimentAnalysisService.analyze(reviewText)).thenReturn(expectedSentiment);

        CompletableFuture<String> sentimentFuture = reviewService.analyzeSentimentAsync(reviewText);

        String actualSentiment = sentimentFuture.join();
        verify(sentimentAnalysisService).analyze(reviewText);

        assertEquals(expectedSentiment, actualSentiment);
    }
}

