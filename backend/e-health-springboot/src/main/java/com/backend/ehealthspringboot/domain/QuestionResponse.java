package com.backend.ehealthspringboot.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class QuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="id")
    private Long id;

    @Column(name="content")
    private String content ;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Question question;

    @ManyToOne
    @JoinColumn(name = "doctor_id" )
    private Doctor doctor;
}
