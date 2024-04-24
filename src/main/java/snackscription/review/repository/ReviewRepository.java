package snackscription.review.repository;

import org.springframework.data.repository.CrudRepository;
import snackscription.review.model.Review;

public interface ReviewRepository extends CrudRepository<Review, String> {}