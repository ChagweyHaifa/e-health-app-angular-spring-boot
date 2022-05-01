package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name="speciality")
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "speciality",targetEntity = Doctor.class)
    private Set<Doctor> doctors;

    @JsonIgnore
    @OneToMany(mappedBy = "speciality")
    private Set<Question> questions;




}
