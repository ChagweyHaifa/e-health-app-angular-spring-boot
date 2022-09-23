package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.DoctorRating;
import com.backend.ehealthspringboot.domain.HttpResponse;
import com.backend.ehealthspringboot.exception.domain.RatingExistExeption;
import com.backend.ehealthspringboot.exception.domain.RatingNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.service.DoctorRatingService;
import com.backend.ehealthspringboot.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.backend.ehealthspringboot.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/ratings")
public class DoctorRatingRessource {

    public static final String RATING_DELETED_SUCCESSFULLY = "You have deleted your rating successfully";

    private DoctorRatingService doctorRatingService;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public DoctorRatingRessource(DoctorRatingService doctorRatingService, JWTTokenProvider jwtTokenProvider){
        this.doctorRatingService = doctorRatingService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("")
    public ResponseEntity<List<DoctorRating>> getAllRatings() {
        List<DoctorRating> ratings = doctorRatingService.getAllRatings();
        return new ResponseEntity(ratings, OK);
    }

    @GetMapping("/search/findByDoctorUsername/{username}")
    public ResponseEntity<List<DoctorRating>> findRatingsByDoctorUsername(@PathVariable("username")String doctorUsername) {
        List<DoctorRating> ratings = doctorRatingService.findRatingsByDoctorUsername(doctorUsername);
        return new ResponseEntity(ratings, OK);
    }

    @PostMapping("")
    public ResponseEntity<Doctor> addRating(HttpServletRequest request , @RequestBody DoctorRating theRating) throws UserNotFoundException, RatingExistExeption {
        String loggedInVisitorUsername = getUsernameFromJWTToken(request);
        Doctor doctor = doctorRatingService.addRating(
                loggedInVisitorUsername,
                theRating.getDoctor().getUsername(),
                theRating.getRating(),
                theRating.getReview()
               );
        return new ResponseEntity<>(doctor, OK);
    }

    @PutMapping("")
    public ResponseEntity<Doctor> updateRating(HttpServletRequest request , @RequestBody DoctorRating theRating) throws RatingNotFoundException  {
        String loggedInVisitorUsername = getUsernameFromJWTToken(request);
        Doctor doctor = doctorRatingService.updateRating(
                loggedInVisitorUsername,
                theRating.getDoctor().getUsername(),
                theRating.getRating(),
                theRating.getReview()
        );
        return new ResponseEntity<>(doctor, OK);
    }


    @DeleteMapping("")
    public ResponseEntity<Doctor> deleteRating(HttpServletRequest request , @RequestParam("doctorUsername") String doctorUsername) throws RatingNotFoundException {
        String loggedInVisitorUsername = getUsernameFromJWTToken(request);
        Doctor doctor = doctorRatingService.deleteRating(loggedInVisitorUsername, doctorUsername);
        return new ResponseEntity<>(doctor, OK);
    }


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
