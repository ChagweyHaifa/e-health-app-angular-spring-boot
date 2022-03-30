package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Entity
@Data
@Table(name="course")

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Course  {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="title")
    private String title;


    @JsonManagedReference
    @ManyToMany(fetch=FetchType.LAZY,
            cascade= {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="course_student",
            joinColumns=@JoinColumn(name="course_id"),
            inverseJoinColumns=@JoinColumn(name="student_id")
    )
    private List<Student> students ;


}
