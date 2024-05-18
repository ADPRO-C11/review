package snackscription.review.model;

import lombok.Data;
import jakarta.persistence.*;


@Data
@Entity
@Table
public class Review {
    @EmbeddedId
    private ReviewId id;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "state", nullable = false)
    private ReviewState state;

    public Review() {
    }

    public Review(int rating, String content, String subsbox, String user) {
        this.id = new ReviewId(subsbox, user);
        this.rating = rating;
        this.content = content;
        this.state = ReviewState.PENDING;
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

    public String getSubsbox() {
        return this.id.getSubsbox();
    }

    public String getAuthor() {
        return this.id.getAuthor();
    }
}