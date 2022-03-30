package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.Visitor;

public interface RecommendationService {

     Doctor recommendDoctor(String theVisitorUsername, String theDoctorUsername) throws Exception;
     Doctor disrecommendDoctor(String theVisitorUsername, String theDoctorUsername) throws Exception;


}
