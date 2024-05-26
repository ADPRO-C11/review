package snackscription.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import io.micrometer.common.lang.Nullable;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewId;
import snackscription.review.model.ReviewState;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    @Nullable
    List<Review> findByIdSubsbox(String subsbox);

    @Nullable
    List<Review> findByIdAuthor(String author);

    @Nullable
    List<Review> findByIdSubsboxAndState(String subsbox, ReviewState state);

    @Nullable
    Review findByIdSubsboxAndIdAuthor(String subsbox, String author);
    
    void deleteByIdSubsboxAndIdAuthor(String subsbox, String author);
}