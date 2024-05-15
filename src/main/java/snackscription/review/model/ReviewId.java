package snackscription.review.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ReviewId implements Serializable {
    @Column(name = "subsbox", nullable = false)
    private String subsbox;

    @Column(name = "author", nullable = false)
    private String author;
    
    public ReviewId(String subsbox, String author) {
        this.subsbox = subsbox;
        this.author = author;
    }
    public ReviewId() {

    }
}
