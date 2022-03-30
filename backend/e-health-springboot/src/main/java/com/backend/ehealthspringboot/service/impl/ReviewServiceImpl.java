package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.repository.ReviewRepository;
import com.backend.ehealthspringboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }
}
