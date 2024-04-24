package snackscription.review.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import snackscription.review.model.Review;

@Repository

public class ReviewRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    //    @Transactional
    public Review save(Review review) {
//        validateRatingInRange(review.getRating());
        entityManager.persist(review);
        return review;
    }
//
//    public Review findById(String id) {
//        return entityManager.find(Review.class, id);
//    }
//
//    @Transactional
//    public Review update(Review review) {
//        validateRatingInRange(review.getRating());
//        return entityManager.merge(review);
//    }
//
//    @Transactional
//    public void delete(Review review) {
//        entityManager.remove(review);
//    }
//
//    private void validateRatingInRange(int rating) {
//        if (rating < 0 || rating > 5) {
//            throw new IllegalArgumentException("Rating must be between 0 and 5 inclusive");
//        }
//    }
}
