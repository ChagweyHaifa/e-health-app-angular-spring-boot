package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="doctor")
public class Doctor extends User {

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "speciality_id" )
    private Speciality speciality;

//    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    Set<DoctorRating> ratings;

    @Column(name = "nb_of_reviews",columnDefinition = "integer default 0")
    private Integer nbOfReviews ;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    List<Review> reviews;

    // convenience methods






}