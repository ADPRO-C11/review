package snackscription.review.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class SentimentAnalysisService {
    public String analyze(String reviewText) {
        // do analysis
        return "positive";
    }

}
