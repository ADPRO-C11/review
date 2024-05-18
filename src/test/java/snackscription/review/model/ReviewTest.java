package snackscription.review.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {
    Review review;

    @BeforeEach
    void setUp() {
        this.review = new Review(
                5,
                "Bagus bgt dah",
                "1",
                "111"
        );
    }

    @Test
    void testCreateReview() {
        int rating = 3;
        String content = "Wow!";
        String userId = "33";
        String subscriptionBoxId = "12345";

        Review newReview = new Review(rating, content, subscriptionBoxId, userId);
        assertEquals(rating, newReview.getRating());
        assertEquals(content, newReview.getContent());
        assertEquals(userId, newReview.getId().getAuthor());
        assertEquals(subscriptionBoxId, newReview.getId().getSubsbox());
        assertNotNull(newReview.getId());
        assertEquals(ReviewState.PENDING, newReview.getState());
    }

    @Test
    void testEditReview() {
        int newRating = 1;
        String newContent = "jelek";
        this.review.editReview(newRating, newContent);
        assertEquals(newRating, this.review.getRating());
        assertEquals(newContent, this.review.getContent());
        assertEquals(ReviewState.PENDING, this.review.getState());
    }

    @Test
    void testEditReviewInvalid() {
        int newRating = -1;
        String newContent = "jelek";
        assertThrows(RuntimeException.class, () -> this.review.editReview(newRating, newContent));
    }

    @Test
    void testApprovePendingReview() {
        this.review.setState(ReviewState.PENDING);
        this.review.approve();
        assertEquals(ReviewState.APPROVED, this.review.getState());
    }

    @Test
    void testRejectPendingReview() {
        this.review.setState(ReviewState.PENDING);
        this.review.reject();
        assertEquals(ReviewState.REJECTED, this.review.getState());
    }

    @Test
    void testApproveApprovedReview() {
        this.review.setState(ReviewState.APPROVED);
        assertThrows(RuntimeException.class, () -> this.review.approve());
        assertEquals(ReviewState.APPROVED, this.review.getState());
    }

    @Test
    void testRejectApprovedReview() {
        this.review.setState(ReviewState.APPROVED);
        this.review.reject();
        assertEquals(ReviewState.REJECTED, this.review.getState());
    }

    @Test
    void testApproveRejectedReview() {
        this.review.setState(ReviewState.REJECTED);
        this.review.approve();
        assertEquals(ReviewState.APPROVED, this.review.getState());
    }

    @Test
    void testRejectRejectedReview() {
        this.review.setState(ReviewState.REJECTED);
        assertThrows(RuntimeException.class, () -> this.review.reject());
        assertEquals(ReviewState.REJECTED, this.review.getState());
    }
}