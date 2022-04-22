package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.domain.ReviewNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;

import java.util.List;


public interface ReviewService {

    List<Review> getDoctorReviews(String username) throws UserNotFoundException;
    Integer addReview(String doctorUsername, String visitorUsername, String content) throws UserNotFoundException;
    Integer  deleteReview(Long reviewId, String loggedInVisitorUsername) throws  ReviewNotFoundException;
}
