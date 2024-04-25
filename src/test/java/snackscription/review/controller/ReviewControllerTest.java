package snackscription.review.controller;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @MockBean
    ReviewService reviewService; 

    @Autowired
    MockMvc mockMvc;

    // @Test
    // public void testGetAllSubscriptionBoxReview() {
    //     String subsboxId = "subsboxId";

    //     ArrayList<Review> reviews = new ArrayList<>();
    //     reviews.add(new Review(5, "amazing", "user1", subsboxId));
    //     reviews.add(new Review(4, "good", "user2", subsboxId));

    //     when(reviewService.testGetAllSubscriptionBoxReview(subsboxId)).thenReturn(reviews);

    //     ResultActions result = mockMvc.perform(get("/api/subscription-boxes/{subsboxId}", subsboxId))
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$", hasSize(2)))
    //         .andExpect(jsonPath("$[0].rating", is(5)))
    //         .andExpect(jsonPath("$[0].content", is("amazing")))
    //         .andExpect(jsonPath("$[0].userId", is("user1")))
    //         .andExpect(jsonPath("$[0].subscriptionBoxId", is(subsboxId)))
    //         .andExpect(jsonPath("$[1].rating", is(4)))
    //         .andExpect(jsonPath("$[1].content", is("good")))
    //         .andExpect(jsonPath("$[1].userId", is("user2")))
    //         .andExpect(jsonPath("$[1].subscriptionBoxId", is(subsboxId)));
        
    //     verify(reviewService).testGetAllSubscriptionBoxReview(subsboxId);
    // }

    @Test
    public void testGetById() throws Exception {
        Review review = new Review(
            5, "amazing", "user1", "subsboxId"
        );
        String reviewId = review.getId();

        when(reviewService.findById(reviewId)).thenReturn(review);
        
        ResultActions result = mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rating", is(5)))
            .andExpect(jsonPath("$.content", is("amazing")))
            .andExpect(jsonPath("$.userId", is("user1")))
            .andExpect(jsonPath("$.subscriptionBoxId", is("subsboxId")));
        
        verify(reviewService).findById(reviewId);
    }
}
