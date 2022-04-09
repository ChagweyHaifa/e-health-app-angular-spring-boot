package com.backend.ehealthspringboot.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

    @Data
    @Entity
    @Table(name="country")
    public class Country  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country",targetEntity = State.class)
    private Set<State> states;

    }
