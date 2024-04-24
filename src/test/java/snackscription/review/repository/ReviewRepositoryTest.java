package snackscription.review.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import snackscription.review.model.Review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@DataJpaTest
public class ReviewRepositoryTest {
//    @Autowired
    private ReviewRepository reviewRepository;
//    @Autowired
    private EntityManager entityManager;
    Review review;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        reviewRepository = new ReviewRepository();
        reviewRepository = mock(ReviewRepository.class);

        review = new Review(5, "Great product!", "user1",  "subsbox1");
    }

    @Test
    public void testSaveReview() {
        doNothing().when(entityManager).persist(review);

        Review savedReview = reviewRepository.save(review);

        assertEquals(savedReview, review);
    }
//
//    @Test
//    void testCreateReview_UnhappyPath_RatingBelowRange() {
//        review.setRating(-1);
//        doNothing().when(entityManager).persist(review);
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            reviewRepository.save(review);
//        });
//    }

//    @Test
//    void testCreateReview_UnhappyPath_RatingAboveRange() {
//        review.setRating(10);
//        doNothing().when(entityManager).persist(review);
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            reviewRepository.save(review);
//        });
//    }
//
//    @Test
//    void testFindReviewById_HappyPath() {
//        when(entityManager.find(Review.class, review.getId())).thenReturn(review);
//
//        Review foundReview = reviewRepository.findById(review.getId());
//
//        assertEquals(foundReview, review);
//        verify(entityManager, times(1)).find(Review.class, review.getId());
//    }
//
//    @Test
//    void testUpdateReview_HappyPath() {
//        when(entityManager.merge(review)).thenReturn(review);
//
//        Review updatedReview = reviewRepository.update(review);
//
//        assertEquals(review, updatedReview);
//        verify(entityManager, times(1)).merge(review);
//    }
//
//    @Test
//    void testDeleteReview_HappyPath() {
//        doNothing().when(entityManager).remove(review);
//
//        reviewRepository.delete(review);
//
//        verify(entityManager, times(1)).remove(review);
//    }
}
