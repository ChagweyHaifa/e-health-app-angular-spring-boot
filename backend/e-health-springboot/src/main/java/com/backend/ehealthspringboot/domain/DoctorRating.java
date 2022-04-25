package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class DoctorRating {

    @EmbeddedId
    DoctorRatingKey id;

    @JsonIgnore
    @ManyToOne
    @MapsId("visitorId")
    @JoinColumn(name = "visitor_id")
    Visitor visitor;

    @JsonIgnore
    @ManyToOne
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    int rating;

    String review;
}
