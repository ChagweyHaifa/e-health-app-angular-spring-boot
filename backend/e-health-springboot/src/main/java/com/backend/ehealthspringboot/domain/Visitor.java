package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
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

    //    the list of doctors recommended by this visitor
    @JsonBackReference
//    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY,
            cascade= {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "recommendations")
    List<Doctor> recommendedDoctors;

    @JsonIgnore
    @OneToMany(mappedBy = "visitor")
    List<Review> reviews;
}
