package snackscription.review.model;

public enum ReviewState {
    PENDING("Pending", new PendingState()),
    APPROVED("Approved", new ApprovedState()),
    REJECTED("Rejected", new RejectedState());

    private final String name;
    private final StateTransition state;
    private ReviewState(String name, StateTransition state) {
        this.name = name;
        this.state = state;
    }

    void approve(Review review) {
        state.approve(review);
    }
    void reject(Review review) {
        state.reject(review);
    }

    @Override
    public String toString() {
        return this.name;
    }

    private interface StateTransition {
        void approve(Review review);
        void reject(Review review);
    }

    private static class PendingState implements StateTransition {
        @Override
        public void approve(Review review) {
            review.setState(APPROVED);
        }

        @Override
        public void reject(Review review) {
            review.setState(REJECTED);
        }
    }

    private static class ApprovedState implements  StateTransition {
        @Override
        public void approve(Review review) {
            throw new RuntimeException("Review already approved.");
        }

        @Override public void reject(Review review) {
            review.setState(REJECTED);
        }
    }

    private static class RejectedState implements  StateTransition {
        @Override
        public void approve(Review review) {
            review.setState(APPROVED);
        }

        @Override public void reject(Review review) {
            throw new RuntimeException("Review already rejected.");
        }
    }
}
