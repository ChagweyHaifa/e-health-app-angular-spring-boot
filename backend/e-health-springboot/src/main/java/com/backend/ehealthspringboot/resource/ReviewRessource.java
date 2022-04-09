package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{username}")
    public ResponseEntity<List<Review>> getDoctorReviews(@PathVariable("username") String username) {
        List<Review> reviews = reviewService.getDoctorReviews(username);
        return new ResponseEntity<>(reviews, OK);
    }

//    @PostMapping("")
//    public ResponseEntity<Review> addReview(Review theReview) {
//        Review review = reviewService.addReview(theReview);
//        return new ResponseEntity<>(review, OK);
//    }
}
