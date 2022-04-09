package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="review")
public class Review  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;


//    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "visitor_id")
    Visitor visitor;

//    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "doctor_id" )
    Doctor doctor;

    @Column(name = "content")
    private String content;

    @Column(name="creation_date")
    @CreationTimestamp
    private Date creationDate;



//    public Review(Visitor visitor, Doctor doctor, String content) {
//        this.visitor = visitor;
//        this.doctor = doctor;
//        this.content = content;
//    }
//
//    public Review() {
//
//    }
}
