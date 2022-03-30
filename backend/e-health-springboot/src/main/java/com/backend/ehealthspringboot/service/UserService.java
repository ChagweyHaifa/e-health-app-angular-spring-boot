package com.backend.ehealthspringboot.service;

import java.io.IOException;
import java.util.List;

import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

public interface UserService {

	User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    User findUserByUsername(String username) ;


    User findUserByEmail(String email);

//    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    List<Student> getStudents();
    List<Course> getCourses();

    List<User> getUsers();
    List<Doctor> getDoctors();

    public Doctor findDoctorByUsername(String theDoctorUsername);
    List<Visitor> getVisitors();



}
