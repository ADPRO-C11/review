package snackscription.review.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import snackscription.review.exception.InvalidStateException;
import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;
import snackscription.review.repository.ReviewRepository;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private SentimentAnalysisService sentimentAnalysisService;

    public ReviewService (ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review findById(String reviewId) throws ReviewNotFoundException {
        Optional<Review> oReview = reviewRepository.findById(reviewId); 

        if (oReview.isEmpty()) {
            throw new ReviewNotFoundException();
        }
        
        return oReview.get(); 
    }

    public List<Review> findBySubscriptionBoxId(String subscriptionBoxId) {
        return reviewRepository.findBySubscriptionBoxId(subscriptionBoxId);
    }

    public Review createReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception {
        Review review = new Review(rating, content, userId, subscriptionBoxId);
        reviewRepository.save(review);
        return review;
    }

    public List<Review> getAllSubscriptionBoxReview(String subscriptionBoxId, String state) throws Exception {
        if (state == null) {
            return reviewRepository.findBySubscriptionBoxId(subscriptionBoxId);
        } else {
            state = state.toUpperCase();
            ReviewState reviewState = Enum.valueOf(ReviewState.class, state);
            if (reviewState == null) {
                throw new InvalidStateException();
            }
            return reviewRepository.findBySubscriptionBoxIdAndState(subscriptionBoxId, reviewState);
        }        
    }

    public Review getReview(String subscriptionBoxId, String userId) throws Exception {
        return reviewRepository.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId);
    }

    public Review editReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception {
        Review review = reviewRepository.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId); 

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        review.setRating(rating);
        review.setContent(content);

        return reviewRepository.save(review);
    }

    public void deleteReview(String subscriptionBoxId, String userId) throws Exception {
        Review review = reviewRepository.findBySubscriptionBoxIdAndUserId(subscriptionBoxId, userId); 

        if (review == null) {
            throw new ReviewNotFoundException();
        }

        reviewRepository.delete(review);
    }

    public Review approveReview(String reviewId) throws Exception {
        Review review = findById(reviewId);
        review.approve();
        return reviewRepository.save(review);
    }

    public Review rejectReview(String reviewId) throws Exception {
        Review review = findById(reviewId);
        review.reject();
        return reviewRepository.save(review);
    }

    @Async
    public CompletableFuture<String> analyzeSentimentAsync(String reviewText) {
        String sentiment = sentimentAnalysisService.analyze(reviewText);
        return CompletableFuture.completedFuture(sentiment);
    }
}
