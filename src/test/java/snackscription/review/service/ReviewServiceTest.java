package snackscription.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import snackscription.review.exception.InvalidStateException;
import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;
import snackscription.review.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    
    @Mock
    ReviewRepository reviewRepo;

    ReviewService reviewService;

    List<Review> reviews;

    @BeforeEach
    public void setUp() {
        reviewService = new ReviewService(reviewRepo);

        Review review1 = new Review(5, "I love it", "subsbox_123", "user_123");
        Review review2 = new Review(1, "I hate it", "subsbox_123", "user_124");
        Review review3 = new Review(2, "Hmmmm idk", "subsbox_124", "user_124");
        Review review4 = new Review(3, "It's okay", "subsbox_124", "user_125");
        Review review5 = new Review(4, "I like it", "subsbox_124", "user_126");

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
    public void testGetSubsboxReview() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubsbox();
        List<Review> curReviews = new ArrayList<>();
        for (Review review : this.reviews) {
            if (review.getSubsbox().equals(subscriptionBoxId)) {
                curReviews.add(review);
            }
        }

        when(reviewRepo.findByIdSubsbox(subscriptionBoxId)).thenReturn(curReviews);

        List<Review> foundReviews = reviewService.getSubsboxReview(subscriptionBoxId, null);

        assertEquals(curReviews, foundReviews);

        verify(reviewRepo).findByIdSubsbox(subscriptionBoxId);
    }

    @Test
    public void testGetSubsboxReviewNotFound() throws Exception {
        String subscriptionBoxId = "nonexistent_subsbox_id";

        when(reviewRepo.findByIdSubsbox(subscriptionBoxId)).thenReturn(null);

        List<Review> foundReviews = reviewService.getSubsboxReview(subscriptionBoxId, null);

        assertNotNull(foundReviews);
        assertEquals(0, foundReviews.size());

        verify(reviewRepo).findByIdSubsbox(subscriptionBoxId);
    }

    @Test
    public void testGetSubsboxReviewApproved() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubsbox();

        List <Review> cuReviews = new ArrayList<>();

        for (Review review : this.reviews) {
            if (review.getSubsbox().equals(subscriptionBoxId) && review.getState().equals(ReviewState.APPROVED)) {
                cuReviews.add(review);
            }
        }

        when(reviewRepo.findByIdSubsboxAndState(subscriptionBoxId, ReviewState.APPROVED)).thenReturn(cuReviews);

        List<Review> foundReviews = reviewService.getSubsboxReview(subscriptionBoxId, "APPROVED");

        assertEquals(cuReviews, foundReviews);

        verify(reviewRepo).findByIdSubsboxAndState(subscriptionBoxId, ReviewState.APPROVED);
    }

    @Test
    public void testGetSubsboxReviewInvalidState() throws Exception {
        String subscriptionBoxId = this.reviews.getFirst().getSubsbox(); 

        assertThrows(InvalidStateException.class, () -> {
            reviewService.getSubsboxReview(subscriptionBoxId, "INVALID_STATE"); 
        }); 
    }

    @Test
    public void testCreateReview() throws Exception {
        Review review = reviews.getFirst();

        when(reviewRepo.save(any(Review.class))).thenReturn(review);

        Review savedReview = reviewService.createReview(
                review.getRating(),
                review.getContent(),
                review.getSubsbox(),
                review.getAuthor());

        assertEqualReview(review, savedReview);

        verify(reviewRepo).save(any(Review.class));
    }

    @Test
    public void testCreateReviewAlreadyExist() throws Exception {
        Review review = reviews.get(0);
        when(reviewRepo.existsById(review.getId())).thenReturn(true);

        assertThrows(Exception.class, () -> {
            reviewService.createReview(review.getRating(), review.getContent(), review.getSubsbox(), review.getAuthor());
        });

        verify(reviewRepo).existsById(review.getId());
    }

    @Test
    public void testCreateReviewInvalidRating() throws Exception {
        Review review = reviews.get(0);
        assertThrows(Exception.class, () -> {
            reviewService.createReview(-1, review.getContent(), review.getSubsbox(), review.getAuthor());
        });
    }

    @Test
    public void testEditReview() throws Exception {
        Review review = reviews.getFirst();
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();
        
        int newRating = 1;
        String newContent = "Changed content";
        Review newReview = new Review(newRating, newContent, author, subsbox);
        newReview.setId(review.getId());

        when(reviewRepo.findByIdSubsboxAndIdAuthor(subsbox, author)).thenReturn(review);
        when(reviewRepo.save(any(Review.class))).thenReturn(newReview);
        
        Review editedReview = reviewService.editReview(newRating, newContent, subsbox, author);

        assertEquals(newRating, editedReview.getRating());
        assertEquals(newContent, editedReview.getContent());
        assertEquals(subsbox, editedReview.getSubsbox());
        assertEquals(author, editedReview.getAuthor());
        assertEquals(review.getId(), editedReview.getId());
    }

    @Test
    public void testEditReviewNotFound() throws Exception {
        Review review = reviews.getFirst();
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();

        int newRating = 1;
        String newContent = "Changed content";
        Review newReview = new Review(newRating, newContent, author, subsbox);
        newReview.setId(review.getId());

        when(reviewRepo.findByIdSubsboxAndIdAuthor(subsbox, author)).thenReturn(null);

        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.editReview(newRating, newContent, subsbox, author);
        }); 
    }

    @Test
    public void testDeleteReview() throws Exception {
        String subsbox = this.reviews.getFirst().getSubsbox();
        String author = this.reviews.getFirst().getAuthor();

        Review review = reviews.getFirst();

        when(reviewRepo.findByIdSubsboxAndIdAuthor(subsbox, author)).thenReturn(review);

        reviewService.deleteReview(subsbox, author);

        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReview(subsbox, author));

        verify(reviewRepo).delete(review);
    }

    @Test
    public void testDeleteReviewNotFound() throws Exception {
        String subsbox = this.reviews.getFirst().getSubsbox();
        String author = this.reviews.getFirst().getAuthor();

        Review review = reviews.getFirst();

        when(reviewRepo.findByIdSubsboxAndIdAuthor(subsbox, author)).thenReturn(null);

        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(subsbox, author));
    }

    public void assertEqualReview(Review review1, Review review2) {
        assertEquals(review1.getRating(), review2.getRating());
        assertEquals(review1.getContent(), review2.getContent());
        assertEquals(review1.getAuthor(), review2.getAuthor());
        assertEquals(review1.getSubsbox(), review2.getSubsbox());
    }
}

