package snackscription.review.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import snackscription.review.model.Review;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void testFindBySubscriptionBoxId() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(5, "Bagus banget", "user_123", "subsbox_123"));
        reviews.add(new Review(1, "Jelek banget", "user_124", "subsbox_123"));

        reviewRepository.saveAll(reviews);

        List <Review> foundReviews = reviewRepository.findBySubscriptionBoxId("subsbox_123");

        assertEquals(reviews.size(), foundReviews.size());
        for (int i=0; i<reviews.size(); i++) {
            assertEqualReview(reviews.get(i), foundReviews.get(i));
        }
    }

    @Test
    public void testFindBySubscriptionBoxIdAndUserId() {
        Review review = new Review(5, "amazing", "user1", "subsboxId");
        reviewRepository.save(review);
        Review foundReview = reviewRepository.findBySubscriptionBoxIdAndUserId("subsboxId", "user1");
        assertEqualReview(review, foundReview);
    }

    public void assertEqualReview(Review review1, Review review2) {
        assertEquals(review1.getRating(), review2.getRating());
        assertEquals(review1.getContent(), review2.getContent());
        assertEquals(review1.getUserId(), review2.getUserId());
        assertEquals(review1.getSubscriptionBoxId(), review2.getSubscriptionBoxId());
    }
}
