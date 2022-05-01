package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.DoctorRating;
import com.backend.ehealthspringboot.exception.domain.RatingExistExeption;
import com.backend.ehealthspringboot.exception.domain.RatingNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;

import java.util.List;

public interface DoctorRatingService {
    List<DoctorRating> getAllRatings();

    List<DoctorRating> findRatingsByDoctorUsername(String doctorUsername);

    Doctor addRating(String loggedInVisitorUsername, String doctorUsername, Integer rating,  String review) throws UserNotFoundException, RatingExistExeption;

    Doctor updateRating(String loggedInVisitorUsername, String doctorUsername, Integer rating, String review) throws RatingNotFoundException;

    Doctor deleteRating(String loggedInVisitorUsername, String doctorUsername) throws RatingNotFoundException;
}
