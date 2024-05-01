package snackscription.review.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import jakarta.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {
    @Id
    private String id;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "state", nullable = false)
    private ReviewState state;

    @Column(name="user_id", nullable = false)
    private String userId;

    @Column(name="subsbox_id", nullable = false)
    private String subscriptionBoxId;

    public Review() {
    }

    public Review(int rating, String content, String userId, String subscriptionBoxId) {
        this.id = UUID.randomUUID().toString();
        this.rating = rating;
        this.content = content;
        this.state = ReviewState.PENDING;
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

    public void approve() {
        this.state.approve(this);
    }

    public void reject() {
        this.state.reject(this);
    }
}