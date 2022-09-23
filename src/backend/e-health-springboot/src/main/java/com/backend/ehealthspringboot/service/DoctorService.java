package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.DoctorRating;

import com.backend.ehealthspringboot.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface DoctorService {



    Doctor register(Doctor doctor) throws Exception;

    List<Doctor> getDoctors();

    Doctor findDoctorByUsername(String username);

    List<Doctor> findDoctorsByAllParameters(Doctor doctor);


    Doctor updateProfileImage(String doctorUsername, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;


    Doctor updateDoctor(String loggedInUsername, String currentDoctorUsername, Doctor theDoctor) throws Exception;
}
