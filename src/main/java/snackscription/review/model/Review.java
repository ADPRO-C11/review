package snackscription.review.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import jakarta.persistence.*;


@Getter
@Entity
@Table(name = "review")
public class Review {
    @Id
    private String id;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Setter
    @Column(name = "content", nullable = false)
    private String content;

//    @Transient
    @Setter
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
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