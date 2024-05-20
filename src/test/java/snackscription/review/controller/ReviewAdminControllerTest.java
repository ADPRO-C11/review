package snackscription.review.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;
import snackscription.review.service.ReviewService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ReviewAdminController.class)
public class ReviewAdminControllerTest {
    @MockBean
    ReviewService reviewService; 

    @Autowired
    MockMvc mockMvc;

    List<Review> reviews;
    static final String BASE_URL = "/admin";

    @BeforeEach
    public void setUp() {
        this.reviews = new ArrayList<>();

        Review review1 = new Review(5, "I love it", "subsbox_123", "user_123");
        Review review2 = new Review(1, "I hate it", "subsbox_123", "user_124");
        Review review3 = new Review(2, "Hmmmm idk", "subsbox_124", "user_124");
        Review review4 = new Review(3, "It's okay", "subsbox_124", "user_125");
        Review review5 = new Review(4, "I like it", "subsbox_124", "user_126");

        review1.setState(ReviewState.PENDING);
        review4.setState(ReviewState.APPROVED);
        review5.setState(ReviewState.REJECTED);

        reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);
        reviews.add(review3);
        reviews.add(review4);
        reviews.add(review5);
    }

    @Test
    public void testApproveReview() throws Exception {
        Review review = reviews.getFirst();

        Review approvedReview = new Review(review.getRating(), review.getContent(), review.getSubsbox(), review.getAuthor());
        approvedReview.setState(ReviewState.APPROVED);

        when(reviewService.approveReview(review.getSubsbox(), review.getAuthor())).thenReturn(approvedReview);

        ResultActions result = mockMvc.perform(put(BASE_URL + "/subscription-boxes/{subsboxId}/users/{userId}/approve", review.getSubsbox(), review.getAuthor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(review.getRating())))
                .andExpect(jsonPath("$.content", is(review.getContent())))
                .andExpect(jsonPath("$.author", is(review.getAuthor())))
                .andExpect(jsonPath("$.subsbox", is(review.getSubsbox())))
                .andExpect(jsonPath("$.state", is("APPROVED")));

        verify(reviewService).approveReview(review.getSubsbox(), review.getAuthor());
    }

    @Test
    public void testRejectReview() throws Exception {
        Review review = reviews.getFirst();

        Review rejectedReview = new Review(review.getRating(), review.getContent(), review.getSubsbox(), review.getAuthor());
        rejectedReview.setState(ReviewState.REJECTED);

        when(reviewService.rejectReview(review.getSubsbox(), review.getAuthor())).thenReturn(rejectedReview);

        ResultActions result = mockMvc.perform(put(BASE_URL + "/subscription-boxes/{subsboxId}/users/{userId}/reject", review.getSubsbox(), review.getAuthor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(review.getRating())))
                .andExpect(jsonPath("$.content", is(review.getContent())))
                .andExpect(jsonPath("$.author", is(review.getAuthor())))
                .andExpect(jsonPath("$.subsbox", is(review.getSubsbox())))
                .andExpect(jsonPath("$.state", is("REJECTED")));

        verify(reviewService).rejectReview(review.getSubsbox(), review.getAuthor());
    }
}
