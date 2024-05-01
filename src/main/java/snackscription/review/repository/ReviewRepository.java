package snackscription.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findBySubscriptionBoxId(String subsboxId);
    List<Review> findBySubscriptionBoxIdAndState(String subsboxId, ReviewState state); 
    Review findBySubscriptionBoxIdAndUserId(String subsboxId, String userId);
    void deleteBySubscriptionBoxIdAndUserId(String subsboxId, String userId);
}