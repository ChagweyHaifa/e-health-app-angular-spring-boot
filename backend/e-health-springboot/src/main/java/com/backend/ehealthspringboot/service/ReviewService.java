package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Review;

import java.util.List;


public interface ReviewService {

    List<Review> getDoctorReviews(String username);
    Review addReview(Review review);
}
