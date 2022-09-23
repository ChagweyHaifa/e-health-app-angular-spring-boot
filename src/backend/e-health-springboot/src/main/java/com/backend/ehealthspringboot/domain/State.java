package com.backend.ehealthspringboot.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name="state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "state",targetEntity = City.class)
    private Set<City> cities;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id" )
    private Country country;
}
