package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class DoctorRating {

    @EmbeddedId
    DoctorRatingKey id = new DoctorRatingKey();

    @ManyToOne
    @MapsId("visitorId")
    @JoinColumn(name = "visitor_id")
    Visitor visitor;

    @ManyToOne
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    @Column(name="rating")
    Integer rating;

    @Column(name="review")
    String review;

    @Column(name="creation_date")
    @CreationTimestamp
    private Date creationDate;
}
