package snackscription.review.model;

public class ReviewStateApproved extends ReviewState {

    ReviewStateApproved(Review review) {
        super(review);
        this.name = "Approved";
    }

    @Override
    public void approve() {
        throw new RuntimeException("Review already approved.");
    }

    @Override
    public void reject() {
        this.review.setState(new ReviewStateRejected(this.review));
    }
}