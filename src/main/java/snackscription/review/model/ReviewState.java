package snackscription.review.model;

public enum ReviewState {
    PENDING(new PendingState()),
    APPROVED(new ApprovedState()),
    REJECTED(new RejectedState());
    private final StateTransition state;
    private ReviewState(StateTransition state) {
        this.state = state;
    }

    void approve(Review review) {
        state.approve(review);
    }
    void reject(Review review) {
        state.reject(review);
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
