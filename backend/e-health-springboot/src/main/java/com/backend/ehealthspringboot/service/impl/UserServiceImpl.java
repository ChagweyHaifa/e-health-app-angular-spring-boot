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
//import javax.persistence.AssociationOverride;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_DOCTOR;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_VISITOR;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.MediaType.*;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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
    private VisitorRepository visitorRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           VisitorRepository visitorRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {

        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.visitorRepository = visitorRepository;
    }

//  all users
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

//  doctors
    @Override
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public List<Doctor> searchDoctors(String speciality,String state){
        return doctorRepository.findBySpecialityNameAndAddressState(speciality,state);
    }
    @Override
    public Doctor findDoctorbyUsername(String username){
        return doctorRepository.findDoctorByUsername(username);
    }

    //  visitors
    @Override
    public List<Visitor> getVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

	@Override
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


	private User userRegister(User theUser) throws UsernameExistException, EmailExistException, MessagingException, UserNotFoundException {
		validateNewUsernameAndEmail(EMPTY, theUser.getUsername(), theUser.getEmail());
        theUser.setUserId(generateUserId());
        String password = generatePassword();
        theUser.setJoinDate(new Date());
        theUser.setPassword(encodePassword(password));
        theUser.setActive(true);
        theUser.setNotLocked(true);
//        emailService.sendNewPasswordEmail(firstName, password, email);
//        set a default image for user
        theUser.setProfileImageUrl(getTemporaryProfileImageUrl(theUser.getUsername()));
        LOGGER.info("New user password: " + password);
        LOGGER.info("user" +theUser);
        return theUser;
	}


//    @Override
//    public void register(User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
//        LOGGER.info("user"+ user);
//
//    }

    @Override
    public Doctor register(Doctor doctor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        doctor = (Doctor) userRegister(doctor);
        doctor.setRole(ROLE_DOCTOR.name());
        doctor.setAuthorities(ROLE_DOCTOR.getAuthorities());
        doctorRepository.save(doctor);
        return doctor;
    }

    @Override
    public Visitor register(Visitor visitor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        visitor = (Visitor) userRegister(visitor);
        visitor.setRole(ROLE_VISITOR.name());
        visitor.setAuthorities(ROLE_VISITOR.getAuthorities());
        visitorRepository.save(visitor);
        return visitor;
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

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

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

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
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

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }

            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
//            if the user directory does not exist
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }

}
