package snackscription.review.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import snackscription.review.exception.InvalidStateException;
import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewId;
import snackscription.review.model.ReviewState;
import snackscription.review.repository.ReviewRepository;

@Service
@Component
public class ReviewService {
    private ReviewRepository reviewRepository;

    public ReviewService (ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception {
        Review review = new Review(rating, content, subscriptionBoxId, userId);
        reviewRepository.save(review);
        return review;
    }

    public Review getReview(String subsbox, String user) throws Exception {
        Optional<Review> oreview = reviewRepository.findById(new ReviewId(user, subsbox));
        if (oreview.isEmpty()) {
            throw new ReviewNotFoundException();
        }
        return oreview.get();
    }

    public List<Review> getSubsboxReview(String subscriptionBoxId, String state) throws Exception {
        if (state == null) {
            return reviewRepository.findByIdSubsbox(subscriptionBoxId);
        } else {
            state = state.toUpperCase();
            ReviewState reviewState = Enum.valueOf(ReviewState.class, state);
            if (reviewState == null) {
                throw new InvalidStateException();
            }
            return reviewRepository.findByIdSubsboxAndState(subscriptionBoxId, reviewState);
        }        
    }

    public Review editReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception {
        Review review = reviewRepository.findByIdSubsboxAndIdAuthor(subscriptionBoxId, userId);

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        review.setRating(rating);
        review.setContent(content);

        return reviewRepository.save(review);
    }

    public Review approveReview(String subsbox, String user) throws Exception {
        Review review = getReview(subsbox, user);
        review.approve();
        return reviewRepository.save(review);
    }

    public Review rejectReview(String subsbox, String user) throws Exception {
        Review review = getReview(subsbox, user);
        review.reject();
        return reviewRepository.save(review);
    }

    public void deleteReview(String subsbox, String user) throws Exception {
        Review review = reviewRepository.findByIdSubsboxAndIdAuthor(subsbox, user);

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        reviewRepository.delete(review);
    }
}
