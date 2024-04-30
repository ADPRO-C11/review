package snackscription.review.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import snackscription.review.model.Review;

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
        reviews.add(new Review(5, "Bagus banget", "user_123", "subsbox_123"));
        reviews.add(new Review(1, "Jelek banget", "user_124", "subsbox_123"));
        reviews.add(new Review(2, "Lorem Ipsum", "user_124", "subsbox_124"));

        reviewRepository.saveAll(reviews);
    }

    @Test
    public void testFindBySubscriptionBoxId() {
        List<Review> curReviews = new ArrayList<>();

        String subsbox_id = this.reviews.getFirst().getSubscriptionBoxId();
        for (Review review : this.reviews) {
            if (review.getSubscriptionBoxId().equals(subsbox_id)) {
                curReviews.add(review);
            }
        }

        List <Review> foundReviews = reviewRepository.findBySubscriptionBoxId(subsbox_id);

        assertEquals(curReviews.size(), foundReviews.size());
        for (int i=0; i<curReviews.size(); i++) {
            assertEqualReview(curReviews.get(i), foundReviews.get(i));
        }
    }

    @Test
    public void testFindBySubscriptionBoxIdAndUserId() {
        Review review = this.reviews.getFirst();

        Review foundReview = reviewRepository.findBySubscriptionBoxIdAndUserId(review.getSubscriptionBoxId(), review.getUserId());
        assertEqualReview(review, foundReview);
    }

    @Test
    public void testDeleteBySubscriptionBoxIdAndUserId() {
        Review review = this.reviews.getFirst();

        reviewRepository.deleteBySubscriptionBoxIdAndUserId(review.getSubscriptionBoxId(), review.getUserId());
        Review foundReview = reviewRepository.findBySubscriptionBoxIdAndUserId(review.getSubscriptionBoxId(), review.getUserId());

        assertNull(foundReview);
    }

    public void assertEqualReview(Review review1, Review review2) {
        assertEquals(review1.getRating(), review2.getRating());
        assertEquals(review1.getContent(), review2.getContent());
        assertEquals(review1.getUserId(), review2.getUserId());
        assertEquals(review1.getSubscriptionBoxId(), review2.getSubscriptionBoxId());
    }
}
