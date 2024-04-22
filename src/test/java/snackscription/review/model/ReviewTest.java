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

        Review newReview = new Review(rating, content, userId, subscriptionBoxId);
        assertEquals(rating, newReview.getRating());
        assertEquals(content, newReview.getContent());
        assertEquals(userId, newReview.getUserId());
        assertEquals(subscriptionBoxId, newReview.getSubscriptionBoxId());
        assertNotNull(newReview.getId());
        assertEquals("Pending", newReview.getState().toString());
    }

    @Test
    void testEditReview() {
        int newRating = 1;
        String newContent = "jelek";
        this.review.editReview(newRating, newContent);
        assertEquals(newRating, this.review.getRating());
        assertEquals(newContent, this.review.getContent());
        assertEquals("Pending", this.review.getState().toString());
    }

    @Test
    void testEditReviewInvalid() {
        int newRating = -1;
        String newContent = "jelek";
        assertThrows(RuntimeException.class, () -> this.review.editReview(newRating, newContent));
    }

    @Test
    void testApprovePendingReview() {
        this.review.setState(new ReviewStatePending(this.review));
        this.review.getState().approve();
        assertEquals("Approved", this.review.getState().toString());
    }

    @Test
    void testRejectPendingReview() {
        this.review.setState(new ReviewStatePending(this.review));
        this.review.getState().reject();
        assertEquals("Rejected", this.review.getState().toString());
    }

    @Test
    void testApproveApprovedReview() {
        this.review.setState(new ReviewStateApproved(this.review));
        assertThrows(RuntimeException.class, () -> this.review.getState().approve());
        assertEquals("Approved", this.review.getState().toString());
    }

    @Test
    void testRejectApprovedReview() {
        this.review.setState(new ReviewStateApproved(this.review));
        this.review.getState().reject();
        assertEquals("Rejected", this.review.getState().toString());
    }

    @Test
    void testApproveRejectedReview() {
        this.review.setState(new ReviewStateRejected(this.review));
        this.review.getState().approve();
        assertEquals("Approved", this.review.getState().toString());
    }

    @Test
    void testRejectRejectedReview() {
        this.review.setState(new ReviewStateRejected(this.review));
        assertThrows(RuntimeException.class, () -> this.review.getState().reject());
        assertEquals("Rejected", this.review.getState().toString());
    }
}