package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.exception.domain.ReviewNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.ReviewRepository;
import com.backend.ehealthspringboot.repository.VisitorRepository;
import com.backend.ehealthspringboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.backend.ehealthspringboot.constant.UserImplConstant.*;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private DoctorRepository doctorRepository;
    private VisitorRepository visitorRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,DoctorRepository doctorRepository,VisitorRepository visitorRepository){
        this.reviewRepository = reviewRepository;
        this.doctorRepository = doctorRepository ;
        this.visitorRepository = visitorRepository;
    }

    @Override
    public List<Review> getDoctorReviews(String doctorUsername) throws UserNotFoundException {
        Doctor doctor = doctorRepository.findDoctorByUsername(doctorUsername);
        if (doctor == null){
            throw new UserNotFoundException(NO_DOCTOR_FOUND_BY_USERNAME + doctorUsername);
        }
        return doctor.getReviews();
    }

    @Override
    public Integer addReview(String doctorUsername, String visitorUsername, String content) throws UserNotFoundException {
        Doctor doctor = doctorRepository.findDoctorByUsername(doctorUsername);
        if (doctor == null){
            throw new UserNotFoundException(NO_DOCTOR_FOUND_BY_USERNAME + doctorUsername);
        }
        Visitor visitor = visitorRepository.findVisitorByUsername(visitorUsername);
        if (visitor == null){
            throw new UserNotFoundException(NO_VISITOR_FOUND_BY_USERNAME + visitorUsername);
        }
        Review newReview = new Review();
        newReview.setVisitor(visitor);
        newReview.setDoctor(doctor);
        newReview.setContent(content);
        doctor.setNbOfReviews(doctor.getNbOfReviews() + 1);
        reviewRepository.save(newReview);
        return doctor.getNbOfReviews();
    }

    @Override
    public Integer deleteReview(Long reviewId, String visitorUsername) throws  ReviewNotFoundException {
       Review review = reviewRepository.findByIdAndVisitorUsername(reviewId, visitorUsername);

       if (review == null )
           throw new ReviewNotFoundException(ACCESS_DENIED_REVIEW_DELETEION);
       else {
           Doctor doctor = review.getDoctor();
           doctor.setNbOfReviews(doctor.getNbOfReviews() - 1);
           reviewRepository.deleteById(reviewId);;
           return doctor.getNbOfReviews();}
    }
}
