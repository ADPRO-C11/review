package snackscription.review.model;

public class ReviewStateRejected extends ReviewState {
    ReviewStateRejected(Review review) {
        super(review);
    }

    @Override
    public void approve() {
        this.review.setState(new ReviewStateApproved(this.review));
    }

    @Override
    public void reject() {
        throw new RuntimeException("Review already rejected.");
    }

    @Override
    public String toString() {
        return "Rejected";
    }
}