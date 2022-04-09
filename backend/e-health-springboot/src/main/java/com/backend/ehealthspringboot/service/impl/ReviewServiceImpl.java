package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.ReviewRepository;
import com.backend.ehealthspringboot.repository.VisitorRepository;
import com.backend.ehealthspringboot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Review> getDoctorReviews(String username) {
        Doctor doctor = doctorRepository.findDoctorByUsername(username);
        return doctor.getReviews();
    }

    public Review addReview(Review review) {
//        Doctor doctor = doctorRepository.findDoctorByUsername("haifa");
//        doctor.setNbOfReviews(doctor.getNbOfReviews()+1);
//        Visitor visitor = visitorRepository.findVisitorByUsername("haithem");
//        Review newReview = new Review();
//        newReview.setVisitor(visitor);
//        newReview.setDoctor(doctor);
//        newReview.setContent("i recommend this doctor");
        return reviewRepository.save(review);
    }
}
