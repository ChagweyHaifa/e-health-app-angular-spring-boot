package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.HttpResponse;
import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.exception.domain.ReviewNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.service.ReviewService;
import com.backend.ehealthspringboot.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.backend.ehealthspringboot.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/reviews")
public class ReviewRessource extends ExceptionHandling {
    public static final String REVIEW_DELETED_SUCCESSFULLY = "REVIEW deleted successfully";

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ReviewService reviewService;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public ReviewRessource(ReviewService reviewService,JWTTokenProvider jwtTokenProvider){
        this.reviewService = reviewService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/search/findByDoctorUsername/{username}")
    public ResponseEntity<List<Review>> getDoctorReviews(@PathVariable("username") String username) throws UserNotFoundException {
        List<Review> reviews = reviewService.getDoctorReviews(username);
        return new ResponseEntity<>(reviews, OK);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('review:create')")
    public ResponseEntity<Integer> addReview(HttpServletRequest request , @RequestBody Review theReview) throws UserNotFoundException  {
        String loggedInVisitorUsername = getUsernameFromJWTToken(request);
       Integer nbOfReviews = reviewService.addReview(
                theReview.getDoctor().getUsername(),
                loggedInVisitorUsername,
                theReview.getContent());
        return new ResponseEntity<>(nbOfReviews, OK);

    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('review:delete')")
    public ResponseEntity<Integer> deleteReview(HttpServletRequest request , @PathVariable("id") Long reviewId) throws ReviewNotFoundException {
        String loggedInVisitorUsername = getUsernameFromJWTToken(request);
        Integer nbOfReviews = reviewService.deleteReview(
                reviewId,
                loggedInVisitorUsername);
        return new ResponseEntity<>(nbOfReviews, OK);

    }

    //  private methods
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    private String getUsernameFromJWTToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
         return   jwtTokenProvider.getSubject(token);
    }
}
