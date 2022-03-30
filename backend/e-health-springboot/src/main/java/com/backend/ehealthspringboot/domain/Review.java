package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
@Data
@Entity
@Table(name="review")
public class Review  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;


//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "visitor_id")
    Visitor visitor;


    @ManyToOne
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    @Column(name = "content")
    private String content;

}
