package snackscription.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewId;
import snackscription.review.model.ReviewState;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    List<Review> findByIdSubsbox(String subsbox);
    List<Review> findByIdAuthor(String author);
    List<Review> findByIdSubsboxAndState(String subsbox, ReviewState state);
    Review findByIdSubsboxAndIdAuthor(String subsbox, String author);
    void deleteByIdSubsboxAndIdAuthor(String subsbox, String author);
}