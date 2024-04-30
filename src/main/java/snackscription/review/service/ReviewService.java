package snackscription.review.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import snackscription.review.exception.ReviewNotFoundException;
import snackscription.review.model.Review;
import snackscription.review.repository.ReviewRepository;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

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

}
