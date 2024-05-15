package snackscription.review.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    List<Review> reviews;

    @BeforeEach
    public void setUp() {
        this.reviews = new ArrayList<>();

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

        reviewRepository.saveAll(reviews);
    }

    @Test
    public void testFindBySubscriptionBoxId() {
        List<Review> curReviews = new ArrayList<>();

        String subsbox_id = this.reviews.getFirst().getSubsbox();
        for (Review review : this.reviews) {
            if (review.getSubsbox().equals(subsbox_id)) {
                curReviews.add(review);
            }
        }

        List <Review> foundReviews = reviewRepository.findByIdSubsbox(subsbox_id);

        assertEquals(curReviews.size(), foundReviews.size());
        for (int i=0; i<curReviews.size(); i++) {
            assertEqualReview(curReviews.get(i), foundReviews.get(i));
        }
    }

    @Test
    public void testFindBySubscriptionBoxIdAndState() {
        List<Review> curReviews = new ArrayList<>();

        String subsbox_id = this.reviews.getFirst().getSubsbox();
        for (Review review : this.reviews) {
            if (review.getSubsbox().equals(subsbox_id) && review.getState().equals(ReviewState.APPROVED)){
                curReviews.add(review);
            }
        }

        List <Review> foundReviews = reviewRepository.findByIdSubsboxAndState(subsbox_id, ReviewState.APPROVED);

        assertEquals(curReviews.size(), foundReviews.size());
        for (int i=0; i<curReviews.size(); i++) {
            assertEqualReview(curReviews.get(i), foundReviews.get(i));
        }
    }

    @Test
    public void testFindBySubscriptionBoxIdAndUserId() {
        Review review = this.reviews.getFirst();
        Review foundReview = reviewRepository.findByIdSubsboxAndIdAuthor(review.getSubsbox(), review.getAuthor());
        assertEqualReview(review, foundReview);
    }

    @Test
    public void testDeleteBySubscriptionBoxIdAndUserId() {
        Review review = this.reviews.getFirst();

        reviewRepository.deleteByIdSubsboxAndIdAuthor(review.getSubsbox(), review.getAuthor());
        Review foundReview = reviewRepository.findByIdSubsboxAndIdAuthor(review.getSubsbox(), review.getAuthor());

        assertNull(foundReview);
    }

    public void assertEqualReview(Review review1, Review review2) {
        assertEquals(review1.getRating(), review2.getRating());
        assertEquals(review1.getContent(), review2.getContent());
        assertEquals(review1.getAuthor(), review2.getAuthor());
        assertEquals(review1.getSubsbox(), review2.getSubsbox());
    }
}
