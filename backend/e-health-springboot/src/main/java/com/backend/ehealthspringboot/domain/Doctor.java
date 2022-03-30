package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="doctor")
public class Doctor extends User {

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "nb_of_recommendations")
    private Integer nbOfRecommendations;

    //    the list of visitors that have recommended this doctor
    @JsonManagedReference
//    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "doctor_recommendation",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "visitor_id"))
    List<Visitor> recommendations;


    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    List<Review> reviews;

    // convenience methods
    public void addRecommendation(Visitor theVisitor) {

        if (recommendations == null) {
            recommendations = new ArrayList<>();
        }
        this.recommendations.add(theVisitor);
        this.nbOfRecommendations++;
    }

    public boolean deleteRecommendation(Visitor theVisitor){
        for(Visitor visitor : this.recommendations) {
            if( visitor.getId() == theVisitor.getId()){
                recommendations.remove(visitor);
                this.nbOfRecommendations--;
                return true;
            }
        }
        return false;

    }

    public Boolean isExistVisitor(Visitor theVisitor) {
        for(Visitor visitor : this.recommendations) {
            if( visitor.getId() == theVisitor.getId()){
                return true;}
        }
        return false;
    }


}