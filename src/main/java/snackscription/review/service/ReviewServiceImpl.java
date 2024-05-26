package snackscription.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.scheduling.annotation.Async;
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
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final SentimentAnalysisService sentimentAnalysisService;

    public ReviewServiceImpl (ReviewRepository reviewRepository, SentimentAnalysisService sentimentAnalysisService) {
        this.reviewRepository = reviewRepository;
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    public boolean reviewExist(String subsbox, String user) {
        return reviewRepository.existsById(new ReviewId(subsbox, user));
    }

    public Review createReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception {
        if (reviewExist(subscriptionBoxId, userId)) {
            throw new Exception("User has made a review for this subscription box.");
        }

        if (rating < 1 || rating > 5) {
            throw new Exception("Rating out of range");
        }

        Review review = new Review(rating, content, subscriptionBoxId, userId);
        reviewRepository.save(review);
        return review;
    }

    public Review getReview(String subsbox, String user) throws Exception {
        Optional<Review> oreview = reviewRepository.findById(new ReviewId(subsbox, user));
        if (oreview.isEmpty()) {
            throw new ReviewNotFoundException();
        }
        return oreview.get();
    }

    public List<Review> getSubsboxReview(String subscriptionBoxId, String state) throws Exception {
        List<Review> result;
        if (state == null) {
            result = reviewRepository.findByIdSubsbox(subscriptionBoxId);
        } else {
            state = state.toUpperCase();
            if (!state.equals("PENDING") && !state.equals("APPROVED") && !state.equals("REJECTED")) {
                throw new InvalidStateException();
            }
            ReviewState reviewState = ReviewState.valueOf(state);
            result = reviewRepository.findByIdSubsboxAndState(subscriptionBoxId, reviewState);
        }
                
        if (result == null) {
            result = new ArrayList<>();
        }
        return result; 
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

    @Async
    public CompletableFuture<String> analyzeSentimentAsync(String reviewText) {
        String sentiment = sentimentAnalysisService.analyze(reviewText);
        return CompletableFuture.completedFuture(sentiment);
    }
}
