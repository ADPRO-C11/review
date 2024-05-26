package snackscription.review.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
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
import org.springframework.test.web.servlet.ResultActions;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import snackscription.review.model.Review;
import snackscription.review.model.ReviewState;
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
    public void testCreateSubsboxReview() throws Exception{
        Review review = reviews.getFirst(); 

        when(reviewService.createReview(review.getRating(), review.getContent(), review.getId().getSubsbox(), review.getId().getAuthor())).thenReturn(review);
        
        ResultActions result = mockMvc.perform(post("/subscription-boxes/{subsbox}/users/self", review.getSubsbox())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"rating\": 5, \"content\": \"I love it\", \"author\": \"user_123\"}"))            
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.rating", is(5)))
            .andExpect(jsonPath("$.content", is("I love it")))
            .andExpect(jsonPath("$.author", is("user_123")))
            .andExpect(jsonPath("$.subsbox", is("subsbox_123")));            

        verify(reviewService).createReview(review.getRating(), review.getContent(), review.getSubsbox(), review.getAuthor());        
    }

    @Test 
    public void testReadAllPublicSubsboxReviews() throws Exception {
        List<Review> approvedReviews = new ArrayList<>();
        String subsbox = "subsbox_124";
        for (Review review : reviews) {           
            if (review.getSubsbox().equals(subsbox) && review.getState().equals(ReviewState.APPROVED)) {
                approvedReviews.add(review);
            }
        }

        when(reviewService.getSubsboxReview(subsbox, "APPROVED")).thenReturn(approvedReviews);       

        String result = mockMvc.perform(get("/subscription-boxes/{subsbox}", subsbox))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(approvedReviews.size())))
               .andReturn()
               .getResponse()
               .getContentAsString();

        List<Review> foundReviews = new ArrayList<Review>();
        for (int i=0; i<approvedReviews.size(); i++) {
            String prefixMatcher = String.format("$[%d]", i);
            int rating = JsonPath.read(result, prefixMatcher + ".rating");
            String content = JsonPath.read(result, prefixMatcher + ".content");
            String author = JsonPath.read(result, prefixMatcher + ".author");
            String curSubscriptionBoxId = JsonPath.read(result, prefixMatcher + ".subsbox");
            
            Review review = new Review(rating, content, curSubscriptionBoxId, author);
            foundReviews.add(review);
        }

       Comparator<Review> cmp = Comparator.comparing(Review::getAuthor);
       approvedReviews.sort(cmp);
       foundReviews.sort(cmp);

       for (int i=0; i<approvedReviews.size(); i++) {
           assertEquals(approvedReviews.get(i).getRating(), foundReviews.get(i).getRating());
           assertEquals(approvedReviews.get(i).getContent(), foundReviews.get(i).getContent());
           assertEquals(approvedReviews.get(i).getAuthor(), foundReviews.get(i).getAuthor());
           assertEquals(approvedReviews.get(i).getSubsbox(), foundReviews.get(i).getSubsbox());           
       }

       verify(reviewService).getSubsboxReview(subsbox, "APPROVED");       
    }

    @Test 
    public void testReadSelfSubsboxReview() throws Exception {
        Review review = reviews.getFirst(); 
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();

        when(reviewService.getReview(subsbox, author)).thenReturn(review);       

        ResultActions result = mockMvc.perform(get("/subscription-boxes/{subscriptionBoxId}/users/self", subsbox, author)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"author\": \"user_123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rating", is(review.getRating())))
            .andExpect(jsonPath("$.content", is(review.getContent())))
            .andExpect(jsonPath("$.author", is(review.getAuthor())))
            .andExpect(jsonPath("$.subsbox", is(review.getSubsbox())));

        verify(reviewService).getReview(subsbox, author);
    }

    @Test
    public void testEditSelfSubsboxReview() throws Exception {
        Review review = reviews.getFirst();
        String subsboxId = review.getSubsbox();
        String userId = review.getAuthor();

        int newRating = 4;
        String newContent = "Awikwok";
        when(reviewService.editReview(newRating, newContent, subsboxId, userId)).thenReturn(new Review(newRating, newContent, subsboxId, userId));

        ResultActions result = mockMvc.perform(put("/subscription-boxes/{subscriptionBoxId}/users/self", subsboxId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"rating\": 4, \"content\": \"Awikwok\", \"author\": \"user_123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rating", is(newRating)))
            .andExpect(jsonPath("$.content", is(newContent)))
            .andExpect(jsonPath("$.author", is(review.getAuthor())))
            .andExpect(jsonPath("$.subsbox", is(review.getSubsbox())));

        verify(reviewService).editReview(newRating, newContent, subsboxId, userId);
    }

    @Test 
    public void testDeleteSelfSubsboxReview() throws Exception {
        Review review = reviews.getFirst();
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();

        doNothing().when(reviewService).deleteReview(subsbox, author);
        doNothing().when(reviewService).deleteReview(subsbox, author);

        ResultActions result = mockMvc.perform(delete("/subscription-boxes/{subsbox}/users/self", subsbox)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"author\": \"user_123\"}"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(subsbox, author);
    }
}
