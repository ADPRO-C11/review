package snackscription.review.service;

import snackscription.review.model.Review;
import java.util.List;

public interface ReviewService {
    public boolean reviewExist(String subsbox, String user);
    public Review createReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception;
    public Review getReview(String subsbox, String user) throws Exception;
    public List<Review> getSubsboxReview(String subscriptionBoxId, String state) throws Exception;
    public Review editReview(int rating, String content, String subscriptionBoxId, String userId) throws Exception;
    public void deleteReview(String subsbox, String user) throws Exception;
    public Review approveReview(String subsbox, String user) throws Exception;
    public Review rejectReview(String subsbox, String user) throws Exception;
}
