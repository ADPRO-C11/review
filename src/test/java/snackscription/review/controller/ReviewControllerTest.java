package snackscription.review.controller;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import snackscription.review.model.Review;
import snackscription.review.service.ReviewService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @MockBean
    ReviewService reviewService; 

    @Autowired
    MockMvc mockMvc;

    List<Review> reviews;

    @BeforeEach
    public void setUp() {
        reviews = new ArrayList<>();
        reviews.add(new Review(5, "Bagus banget", "user_123", "subsbox_123"));
        reviews.add(new Review(1, "Jelek banget", "user_124", "subsbox_123"));
        reviews.add(new Review(2, "Lorem Ipsum", "user_124", "subsbox_124"));
    }

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

    @Test
    public void testGetBySubscriptionBoxId() throws Exception {
        List<Review> curReviews = new ArrayList<>();

        String subscriptionBoxId = this.reviews.getFirst().getSubscriptionBoxId();
        for (Review review : this.reviews) {
            if (review.getSubscriptionBoxId().equals(subscriptionBoxId)) {
                curReviews.add(review);
            }
        }

        when(reviewService.findBySubscriptionBoxId(subscriptionBoxId)).thenReturn(curReviews);

        String result = mockMvc.perform(get("/api/subscription-boxes/{subscriptionBoxId}", subscriptionBoxId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(curReviews.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

       List<Review> foundReviews = new ArrayList<Review>();
        for (int i=0; i<curReviews.size(); i++) {
            String prefixMatcher = String.format("$[%d]", i);
            int rating = JsonPath.read(result, prefixMatcher + ".rating");
            String content = JsonPath.read(result, prefixMatcher + ".content");
            String userId = JsonPath.read(result, prefixMatcher + ".userId");
            String curSubscriptionBoxId = JsonPath.read(result, prefixMatcher + ".subscriptionBoxId");

            Review review = new Review(rating, content, userId, curSubscriptionBoxId);
            foundReviews.add(review);
        }

        Comparator<Review> cmp = new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                return o1.getUserId().compareTo(o2.getUserId());
            }
        };

        curReviews.sort(cmp);
        foundReviews.sort(cmp);

        for (int i=0; i<curReviews.size(); i++) {
            assertEquals(curReviews.get(i).getRating(), foundReviews.get(i).getRating());
            assertEquals(curReviews.get(i).getContent(), foundReviews.get(i).getContent());
            assertEquals(curReviews.get(i).getUserId(), foundReviews.get(i).getUserId());
            assertEquals(curReviews.get(i).getSubscriptionBoxId(), foundReviews.get(i).getSubscriptionBoxId());
        }

        verify(reviewService).findBySubscriptionBoxId(subscriptionBoxId);
    }
}
