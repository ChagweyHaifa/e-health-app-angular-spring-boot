package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="visitor")
public class Visitor extends User{

    @Column(name="question")
    private String question;

    @JsonIgnore
    @OneToMany(mappedBy = "visitor")
    Set<DoctorRating> ratings;

    @JsonIgnore
    @OneToMany(mappedBy = "visitor")
    List<Review> reviews;

    public void addReview(Review review){
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
    }
}
