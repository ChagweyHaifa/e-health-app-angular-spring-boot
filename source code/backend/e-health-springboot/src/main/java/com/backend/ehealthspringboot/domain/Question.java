package com.backend.ehealthspringboot.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "speciality_id" )
    private Speciality speciality;

    @ManyToOne
    @JoinColumn(name = "user_id" )
    private User user;

    @Column(name="title")
    private String title;

    @Column(name="question")
    private String question;

    @Column(name="current_treatment")
    private String currentTreatment;

    @Column(name="medical_history")
    private String medicalHistory;

    @Column(name="questioner_height")
    private int questionerHeight;

    @Column(name="questioner_weight")
    private int questionerWeight;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    private QuestionResponse response;






}
