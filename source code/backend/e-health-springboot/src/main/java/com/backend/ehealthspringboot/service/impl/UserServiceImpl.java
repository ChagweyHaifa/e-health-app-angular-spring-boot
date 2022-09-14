package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.enumeration.Role;
import com.backend.ehealthspringboot.exception.domain.*;
//import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.*;
import com.backend.ehealthspringboot.service.EmailService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.backend.ehealthspringboot.service.LoginAttemptService;
import com.backend.ehealthspringboot.service.UserService;

import javax.mail.MessagingException;


import static com.backend.ehealthspringboot.enumeration.Role.ROLE_ADMIN;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_USER;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.MediaType.*;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.*;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService  {
	
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private UserRepository userRepository;
    private DoctorRepository doctorRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
//    this funtion is executed when the user trys to log in
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(User user) throws Exception {
        validateNewUsernameAndEmail(EMPTY, user.getUsername(), user.getEmail());


        user.setJoinDate(new Date());
        LOGGER.info("New doctor password: " + user.getPassword());
        if(user.getPassword() ==null || user.getPassword() == ""){
            throw new Exception("password is required");
        }
       user.setPassword(encodePassword(user.getPassword()));
        user.setActive(true);
        user.setNotLocked(true);
//        emailService.sendNewPasswordEmail(firstName, password, email);
//        set a default image for user

        user.setRole(ROLE_USER.name());

        userRepository.save(user);
        return user;
    }



    @Override
    public User updateUser(String loggedInUsername, String currentUsername, User theUser) throws UserNotFoundException, EmailExistException, UsernameExistException {

        User user = this.findUserByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        User newUser;
        if((getRoleEnumName(user.getRole()) == ROLE_ADMIN )){
            newUser = validateNewUsernameAndEmail(currentUsername ,theUser.getUsername(),theUser.getEmail());
            newUser.setRole(getRoleEnumName(theUser.getRole()).name());
            LOGGER.info("admin");
        }
        else {
            LOGGER.info("user");
            newUser = validateNewUsernameAndEmail(loggedInUsername ,theUser.getUsername(),theUser.getEmail());
        }
        newUser.setFirstName(theUser.getFirstName());
        LOGGER.info(theUser.getFirstName());
        newUser.setLastName(theUser.getLastName());
        newUser.setUsername(theUser.getUsername());
        newUser.setPhoneNumber(theUser.getPhoneNumber());
        newUser.setEmail(theUser.getEmail());
        newUser.setGender(theUser.getGender());
        newUser.setAddress(theUser.getAddress());
        newUser.setActive(theUser.isActive());
        newUser.setNotLocked(theUser.isNotLocked());
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {

        return userRepository.findByRole(role);

    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }



//    @Override
//    public User addNewUser(String firstName, String lastName, String username, String email,
//                           String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
//            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
//        validateNewUsernameAndEmail(EMPTY, username, email);
//        User user = new User();
//        String password = generatePassword();
//        user.setUserId(generateUserId());
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setJoinDate(new Date());
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPassword(encodePassword(password));
//        user.setActive(isActive);
//        user.setNotLocked(isNonLocked);
//        user.setRole(getRoleEnumName(role).name());
//        user.setAuthorities(getRoleEnumName(role).getAuthorities());
//        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
//        userRepository.save(user);
//        saveProfileImage(user, profileImage);
//        LOGGER.info("New user password: " + password);
//        return user;
//    }

//    @Override
//    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
//        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
//        currentUser.setFirstName(newFirstName);
//        currentUser.setLastName(newLastName);
//        currentUser.setUsername(newUsername);
//        currentUser.setEmail(newEmail);
//        currentUser.setActive(isActive);
//        currentUser.setNotLocked(isNonLocked);
//        currentUser.setRole(getRoleEnumName(role).name());
//        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
//        userRepository.save(currentUser);
//        return currentUser;
//    }

    @Override
    public void deleteUser(String username) throws IOException {
        User user = userRepository.findUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        LOGGER.info("New user password: " + password);
//        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }




//    private methods
    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }



	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
		User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
//      updating case
        if(StringUtils.isNotBlank(currentUsername)) {
//            get current user info from DB
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }
//        in adding case
        else {
        	
            if(userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
	
    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }
    
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
    
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }





}
