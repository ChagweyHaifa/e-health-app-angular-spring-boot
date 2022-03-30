package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.repository.*;
import com.backend.ehealthspringboot.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private DoctorRepository doctorRepository;
    private VisitorRepository visitorRepository;

    @Autowired
    public RecommendationServiceImpl(DoctorRepository doctorRepository,
                                     VisitorRepository visitorRepository){
        this.doctorRepository = doctorRepository;
        this.visitorRepository = visitorRepository;

    }

    @Override
    public Doctor recommendDoctor(String loggedInUsername, String theDoctorUsername) throws Exception {
       Doctor doctor = doctorRepository.findDoctorByUsername(theDoctorUsername);
       Visitor visitor = visitorRepository.findVisitorByUsername(loggedInUsername);
       boolean isExistVisitor = doctor.isExistVisitor(visitor);
       if (isExistVisitor){
           throw new Exception("you have already recommended this doctor");
       }
       else {
           doctor.addRecommendation(visitor);
           doctorRepository.save(doctor);
           return doctor;
       }
    }

    @Override
    public Doctor disrecommendDoctor(String loggedInUsername, String theDoctorUsername) throws Exception {
        Doctor doctor = doctorRepository.findDoctorByUsername(theDoctorUsername);
        Visitor visitor = visitorRepository.findVisitorByUsername(loggedInUsername);
        boolean result = doctor.deleteRecommendation(visitor);
        if (result = false)
            throw new Exception("you have already disrecommended this doctor");
        doctorRepository.save(doctor);
        return doctor;
    }


}
