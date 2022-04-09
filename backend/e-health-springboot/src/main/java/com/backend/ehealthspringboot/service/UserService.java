package com.backend.ehealthspringboot.service;

import java.io.IOException;
import java.util.List;

import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

public interface UserService {

//    void register(User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    Doctor register(Doctor doctor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    Visitor register(Visitor visitor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    User findUserByUsername(String username) ;

    User findUserByEmail(String email);

//    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;



    List<User> getUsers();
    List<Doctor> getDoctors();
    List<Doctor> searchDoctors(String speciality,String state);
    Doctor findDoctorbyUsername(String username);
    List<Visitor> getVisitors();



}
