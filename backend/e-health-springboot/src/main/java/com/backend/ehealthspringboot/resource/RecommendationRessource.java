package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Doctor;

import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/recommendations")
public class RecommendationRessource extends ExceptionHandling {

    private RecommendationService recommendationService;

    @Autowired
    public RecommendationRessource(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendDoctor/{loggedInusername}")
    public ResponseEntity<Doctor> recommendDoctor(@PathVariable("loggedInusername") String loggedInUsername, @RequestBody Doctor theDoctor ) throws Exception {
        Doctor doctor = recommendationService.recommendDoctor(loggedInUsername,theDoctor.getUsername());
        return new ResponseEntity<>(doctor, OK);
    }

    @PostMapping("/disrecommendDoctor/{loggedInusername}")
    public ResponseEntity<Doctor> disrecommendDoctor(@PathVariable("loggedInusername") String loggedInUsername, @RequestBody Doctor theDoctor ) throws Exception {
        Doctor doctor = recommendationService.disrecommendDoctor(loggedInUsername,theDoctor.getUsername());
        return new ResponseEntity<>(doctor, OK);
    }

}
