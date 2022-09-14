package com.backend.ehealthspringboot.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;


import java.util.List;
import java.util.Set;

@Entity
@Data
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@DiscriminatorValue(value="doctor")
public class Doctor extends User {


    @Column(name="status")
    private String status;

    @Column(name="profile_image_url")
    private String profileImageUrl;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "speciality_id" )
    private Speciality speciality;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    List<DoctorRating> doctorRatings;

    @Column(name = "nb_of_ratings",columnDefinition = "Integer default 0")
    private Integer nbOfRatings = 0;

    @Column(name = "average_of_ratings",columnDefinition = "float default 0")
    private float averageOfRatings = 0;;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private Set<QuestionResponse> responses;

    // convenience methods
    public Integer incrementNbOfRatings(){
        this.nbOfRatings = this.nbOfRatings + 1;
        return this.nbOfRatings;
    }

    public Integer decrementNbOfRatings(){
        this.nbOfRatings = this.nbOfRatings - 1;
        return  this.nbOfRatings;
    }


//    public void addRating(DoctorRating rating){
//        if (rating!= null){
//            if(ratings == null){
//                ratings = new ArrayList<>();
//            }
//            this.ratings.add(rating);
//            this.nbOfRatings = this.nbOfRatings + 1;
//
//        }
//
//    }
    public void calculateAverageOfRating(){

        if(this.doctorRatings==null){
            this.ratings =  new ArrayList<>();
        }

        if(this.doctorRatings.size() != 0){
            float sum = 0;
            for (int i = 0; i < this.doctorRatings.size(); i++){
                sum = sum + this.doctorRatings.get(i).getRating();
            }
            this.averageOfRatings = sum/this.doctorRatings.size();
        }
        else{
            this.averageOfRatings = 0;
        }


    }






}