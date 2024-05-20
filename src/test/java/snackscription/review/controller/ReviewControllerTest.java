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
        
        ResultActions result = mockMvc.perform(post("/subscription-boxes/{subsbox}", review.getSubsbox())
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

        String result = mockMvc.perform(get("/subscription-boxes/{subsbox}/public", subsbox))
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
    public void readSelfSubscriptionBoxReview() throws Exception {
        Review review = reviews.getFirst(); 
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();

        when(reviewService.getReview(subsbox, author)).thenReturn(review);

        ResultActions result = mockMvc.perform(get("/subscription-boxes/{subscriptionBoxId}/users/{author}", subsbox, author)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"author\": \"user_123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rating", is(review.getRating())))
            .andExpect(jsonPath("$.content", is(review.getContent())))
            .andExpect(jsonPath("$.author", is(review.getAuthor())))
            .andExpect(jsonPath("$.subsbox", is(review.getSubsbox())));

        verify(reviewService).getReview(subsbox, author);
    }

//    @Test
//    public void testEditSelfSubscriptionBoxReview() throws Exception {
//        Review review = reviews.getFirst();
//        String subsboxId = review.getSubsbox();
//        String userId = review.getAuthor();
//
//        int newRating = 4;
//        String newContent = "Awikwok";
//        when(reviewService.editReview(newRating, newContent, subsboxId, userId)).thenReturn(new Review(newRating, newContent, userId, subsboxId));
//
//        ResultActions result = mockMvc.perform(put("/reviews/subscription-boxes/{subscriptionBoxId}/users/self", subsboxId)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{\"rating\": 4, \"content\": \"Awikwok\", \"userId\": \"user_123\"}"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.rating", is(newRating)))
//            .andExpect(jsonPath("$.content", is(newContent)))
//            .andExpect(jsonPath("$.userId", is(review.getAuthor())))
//            .andExpect(jsonPath("$.subscriptionBoxId", is(review.getSubsbox())));
//
//        verify(reviewService).editReview(newRating, newContent, subsboxId, userId);
//    }
//
//    @Test
//    public void testDeleteSelfSubscriptionBoxReview() throws Exception {
//        Review review = reviews.getFirst();
//        String subsboxId = review.getSubsbox();
//        String userId = review.getAuthor();
//
//        doNothing().when(reviewService).deleteReview(subsboxId, userId);
//
//        ResultActions result = mockMvc.perform(delete("/subscription-boxes/{subscriptionBoxId}/users/self", subsboxId)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("{\"userId\": \"user_123\"}"))
//            .andExpect(status().isNoContent());
//
//        verify(reviewService).deleteReview(subsboxId, userId);
//    }

    @Test 
    public void testDeleteUserSubscriptionBoxReview() throws Exception {
        Review review = reviews.getFirst();
        String subsbox = review.getSubsbox();
        String author = review.getAuthor();

        doNothing().when(reviewService).deleteReview(subsbox, author);

        ResultActions result = mockMvc.perform(delete("/subscription-boxes/{subsbox}/users/{author}", subsbox, author)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(subsbox, author);
    }

//     @Test
//     public void testGetAllSubscriptionBoxReview() {
//         String subsboxId = "subsboxId";

//         ArrayList<Review> reviews = new ArrayList<>();
//         reviews.add(new Review(5, "amazing", "user1", subsboxId));
//         reviews.add(new Review(4, "good", "user2", subsboxId));

//         when(reviewService.testGetAllSubscriptionBoxReview(subsboxId)).thenReturn(reviews);

//         ResultActions result = mockMvc.perform(get("/api/subscription-boxes/{subsboxId}", subsboxId))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$", hasSize(2)))
//             .andExpect(jsonPath("$[0].rating", is(5)))
//             .andExpect(jsonPath("$[0].content", is("amazing")))
//             .andExpect(jsonPath("$[0].userId", is("user1")))
//             .andExpect(jsonPath("$[0].subscriptionBoxId", is(subsboxId)))
//             .andExpect(jsonPath("$[1].rating", is(4)))
//             .andExpect(jsonPath("$[1].content", is("good")))
//             .andExpect(jsonPath("$[1].userId", is("user2")))
//             .andExpect(jsonPath("$[1].subscriptionBoxId", is(subsboxId)));
        
//         verify(reviewService).testGetAllSubscriptionBoxReview(subsboxId);
//     }

//    @Test
//    public void testGetById() throws Exception {
//        Review review = new Review(
//            5, "amazing", "user1", "subsboxId"
//        );
//        String reviewId = review.getId();

//        when(reviewService.findById(reviewId)).thenReturn(review);

//        ResultActions result = mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.rating", is(5)))
//            .andExpect(jsonPath("$.content", is("amazing")))
//            .andExpect(jsonPath("$.userId", is("user1")))
//            .andExpect(jsonPath("$.subscriptionBoxId", is("subsboxId")));

//        verify(reviewService).findById(reviewId);
//    }

//    @Test
//    public void testGetBySubscriptionBoxId() throws Exception {
//        List<Review> curReviews = new ArrayList<>();

//        String subscriptionBoxId = this.reviews.getFirst().getSubsbox();
//        for (Review review : this.reviews) {
//            if (review.getSubsbox().equals(subscriptionBoxId)) {
//                curReviews.add(review);
//            }
//        }

//        when(reviewService.findBySubscriptionBoxId(subscriptionBoxId)).thenReturn(curReviews);

//        String result = mockMvc.perform(get("/api/subscription-boxes/{subscriptionBoxId}", subscriptionBoxId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(curReviews.size())))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();

//       List<Review> foundReviews = new ArrayList<Review>();
//        for (int i=0; i<curReviews.size(); i++) {
//            String prefixMatcher = String.format("$[%d]", i);
//            int rating = JsonPath.read(result, prefixMatcher + ".rating");
//            String content = JsonPath.read(result, prefixMatcher + ".content");
//            String userId = JsonPath.read(result, prefixMatcher + ".userId");
//            String curSubscriptionBoxId = JsonPath.read(result, prefixMatcher + ".subscriptionBoxId");

//            Review review = new Review(rating, content, userId, curSubscriptionBoxId);
//            foundReviews.add(review);
//        }

//        Comparator<Review> cmp = new Comparator<Review>() {
//            @Override
//            public int compare(Review o1, Review o2) {
//                return o1.getAuthor().compareTo(o2.getAuthor());
//            }
//        };

//        curReviews.sort(cmp);
//        foundReviews.sort(cmp);

//        for (int i=0; i<curReviews.size(); i++) {
//            assertEquals(curReviews.get(i).getRating(), foundReviews.get(i).getRating());
//            assertEquals(curReviews.get(i).getContent(), foundReviews.get(i).getContent());
//            assertEquals(curReviews.get(i).getAuthor(), foundReviews.get(i).getAuthor());
//            assertEquals(curReviews.get(i).getSubsbox(), foundReviews.get(i).getSubsbox());
//        }

//        verify(reviewService).findBySubscriptionBoxId(subscriptionBoxId);
//    }
}
