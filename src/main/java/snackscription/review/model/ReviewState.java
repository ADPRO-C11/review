package snackscription.review.model;

import jakarta.persistence.*;


@Entity
@Table(name = "review_state")
public abstract class ReviewState {

    @Transient
    Review review;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; 

    @Column(nullable = false)
    String name; 

    ReviewState(Review review) {
        this.review = review;
    }

    public abstract void  approve();

    public abstract void reject();

    @Override
    public String toString() {
        return this.name;
    }
}