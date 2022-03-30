package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/reviews")
public class ReviewRessource extends ExceptionHandling {

    private ReviewService reviewService;

    @Autowired
    public ReviewRessource(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @GetMapping("")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getReviews();
        return new ResponseEntity<>(reviews, OK);
    }
}
