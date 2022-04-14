package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;

import java.util.List;


public interface ReviewService {

    List<Review> getDoctorReviews(String username);
    Review addReview(String doctorUsername, String visitorUsername, String content) throws UserNotFoundException;
}
