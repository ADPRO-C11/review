package snackscription.review.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Review {
    private final String id;
    private int rating;
    @Setter
    private String content;
    @Setter
    private ReviewState state;
    private final String userId;
    private final String subscriptionBoxId;

    public Review(int rating, String content, String userId, String subscriptionBoxId) {
        this.id = UUID.randomUUID().toString();
        this.rating = rating;
        this.content = content;
        this.state = new ReviewStatePending(this);
        this.userId = userId;
        this.subscriptionBoxId = subscriptionBoxId;
    }

    public void editReview(int rating, String content) {
        this.setRating(rating);
        this.setContent(content);
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new RuntimeException("Rating should be between 0 and 5.");
        }
        this.rating = rating;
    }
}