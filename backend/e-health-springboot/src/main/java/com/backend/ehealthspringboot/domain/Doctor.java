package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="doctor")
public class Doctor extends User {

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "speciality_id" )
    private Speciality speciality;

    @Column(name = "nb_of_recommendations",columnDefinition = "integer default 0")
    private Integer nbOfRecommendations ;

    // the list of visitors that have recommended this doctor
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "doctor_recommendation",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "visitor_id"))
    List<Visitor> recommendations;


    @Column(name = "nb_of_reviews",columnDefinition = "integer default 0")
    private Integer nbOfReviews ;

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

//    public void addReview(Visitor visitor,String content){
//
//        if (this.reviews == null) {
//            this.reviews = new ArrayList<>();
//        }
//        this.reviews.add(new Review(visitor,this,content));
//        this.nbOfReviews++;
//
//    }



}