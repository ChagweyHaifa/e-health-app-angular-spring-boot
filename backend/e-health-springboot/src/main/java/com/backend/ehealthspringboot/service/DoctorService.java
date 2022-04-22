package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.NotAnImageFileException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface DoctorService {

    Doctor register(Doctor doctor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    List<Doctor> getDoctors();

    Doctor findDoctorByUsername(String username);

    List<Doctor> findDoctorsByAllParameters(Doctor doctor);

    Doctor updateDoctor(String doctorUsername,Doctor doctor) throws UserNotFoundException, EmailExistException, UsernameExistException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;




}
