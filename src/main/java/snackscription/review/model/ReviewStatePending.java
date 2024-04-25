package snackscription.review.model;

public class ReviewStatePending extends ReviewState {

    ReviewStatePending(Review review) {
        super(review);
        this.name = "Pending";
    }

    @Override
    public void approve() {
        this.review.setState(new ReviewStateApproved(this.review));
    }

    @Override
    public void reject() {
        this.review.setState(new ReviewStateRejected(this.review));
    }
}