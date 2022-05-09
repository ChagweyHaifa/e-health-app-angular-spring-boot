package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="visitor")
public class Visitor extends User{

    @JsonIgnore
    @OneToMany(mappedBy = "visitor")
    Set<DoctorRating> ratings;


}
