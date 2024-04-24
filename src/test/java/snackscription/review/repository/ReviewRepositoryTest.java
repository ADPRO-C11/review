package snackscription.review.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import snackscription.review.model.Review;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewRepositoryTest {
    ReviewRepository reviewRepository;
    Review review;

    @BeforeEach
    void setUp() {
        reviewRepository = Mockito.mock(ReviewRepository.class);
        this.review = new Review(
                5,
                "Bagus bgt dah",
                "1",
                "111"
        );
    }
    @Test
    void testSaveReview_HappyPath() {
        Review savedReview = reviewRepository.save(review);
        assertEquals(savedReview, review);
    }

    @Test
    void testCreateReview_UnhappyPath_RatingBelowRange() {
        review.setRating(0);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewRepository.save(review);
        });
    }

    @Test
    void testCreateReview_UnhappyPath_RatingAboveRange() {
        review.setRating(10);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewRepository.save(review);
        });
    }

    @Test
    void testFindReviewById_HappyPath() {
        Review savedReview = reviewRepository.save(review);


    }

    @Test
    void testGetReviewByIdInvalid() {}

    @Test
    void testUpdateReview() {}

    @Test
    void testUpdateReviewInvalid() {}

    @Test
    void testDeleteReviewById() {}

    @Test
    void testDeleteReviewByIdInvalid() {}

}
