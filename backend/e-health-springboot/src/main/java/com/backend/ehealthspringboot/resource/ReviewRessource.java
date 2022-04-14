package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/reviews")
public class ReviewRessource extends ExceptionHandling {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
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

    @PostMapping("")
//    get the the loggedInUser username from the token
    public ResponseEntity<Review> addReview(@RequestBody Review theReview) throws UserNotFoundException {
        Review review = reviewService.addReview(
                theReview.getDoctor().getUsername(),
                theReview.getVisitor().getUsername(),
                theReview.getContent());
        return new ResponseEntity<>(review, OK);
    }
}
