package com.backend.ehealthspringboot.domain;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @Column(name="country")
    private String country;

    @Column(name="state")
    private String state;

    @Column(name="city")
    private String city;

    @Column(name="street")
    private String street;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

}
