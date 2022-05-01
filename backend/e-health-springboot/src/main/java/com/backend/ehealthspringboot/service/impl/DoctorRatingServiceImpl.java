package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.DoctorRating;
import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.exception.domain.RatingExistExeption;
import com.backend.ehealthspringboot.exception.domain.RatingNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.repository.DoctorRatingRepository;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.VisitorRepository;
import com.backend.ehealthspringboot.service.DoctorRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.backend.ehealthspringboot.constant.UserImplConstant.*;

@Service
public class DoctorRatingServiceImpl implements DoctorRatingService {

    private DoctorRatingRepository doctorRatingRepository;
    private DoctorRepository doctorRepository;
    private VisitorRepository visitorRepository;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    public DoctorRatingServiceImpl(DoctorRatingRepository doctorRatingRepository,DoctorRepository doctorRepository, VisitorRepository visitorRepository) {
        this.doctorRatingRepository = doctorRatingRepository;
        this.doctorRepository = doctorRepository;
        this.visitorRepository = visitorRepository;

    }
    @Override
    public List<DoctorRating> getAllRatings(){
        return doctorRatingRepository.findAll();

    }

    @Override
    public List<DoctorRating> findRatingsByDoctorUsername(String doctorUsername) {
        return doctorRatingRepository.findByDoctorUsername(doctorUsername);
    }

    @Override
    public Doctor addRating(String loggedInVisitorUsername, String doctorUsername, Integer theRating,  String review) throws UserNotFoundException, RatingExistExeption {
        Doctor doctor = doctorRepository.findDoctorByUsername(doctorUsername);
        if (doctor == null){
            throw new UserNotFoundException(NO_DOCTOR_FOUND_BY_USERNAME + doctorUsername);
        }
        Visitor visitor = visitorRepository.findVisitorByUsername(loggedInVisitorUsername);
        if (visitor == null){
            throw new UserNotFoundException(NO_VISITOR_FOUND_BY_USERNAME + loggedInVisitorUsername);
        }
        DoctorRating rating = doctorRatingRepository.findByVisitorUsernameAndDoctorUsername(loggedInVisitorUsername, doctorUsername);
        if (rating != null){
            throw new RatingExistExeption(Rating_ALREADY_EXISTS);
        }
        DoctorRating doctorRating = new DoctorRating();
        doctorRating.setVisitor(visitor);
        doctorRating.setDoctor(doctor);
        doctorRating.setRating(theRating);
        doctorRating.setReview(review);
//        doctor.addRating(doctorRating);
        doctorRatingRepository.save(doctorRating);
        doctor.incrementNbOfRatings();
        doctor.calculateAverageOfRating();
        doctorRepository.save(doctor);
        return doctor;
    }

    @Override
    public Doctor updateRating(String loggedInVisitorUsername, String doctorUsername, Integer rating, String review) throws RatingNotFoundException {
        DoctorRating theRating = doctorRatingRepository.findByVisitorUsernameAndDoctorUsername(loggedInVisitorUsername, doctorUsername);
        if (theRating == null){
            throw new RatingNotFoundException(NO_Rating_FOUND);
        }
        theRating.setRating(rating);
        theRating.setReview(review);
        doctorRatingRepository.save(theRating);
        Doctor doctor = theRating.getDoctor();
        doctor.calculateAverageOfRating();
        LOGGER.info("average" + doctor.getAverageOfRatings());
        doctorRepository.save(doctor);
        return doctor ;
    }

    @Override
    public Doctor deleteRating(String loggedInVisitorUsername, String doctorUsername) throws RatingNotFoundException {
        DoctorRating theRating = doctorRatingRepository.findByVisitorUsernameAndDoctorUsername(loggedInVisitorUsername, doctorUsername);
        if (theRating == null){
            throw new RatingNotFoundException(NO_Rating_FOUND);
        }

        Doctor doctor = theRating.getDoctor();
//        LOGGER.info("size before deletion" + doctor.getRatings().size());
        doctorRatingRepository.delete(theRating);
//        LOGGER.info("size after deltetion" + doctor.getRatings());
        doctor.decrementNbOfRatings();
        doctor.calculateAverageOfRating();
        doctorRepository.save(doctor);
        return doctor;
    }
}
