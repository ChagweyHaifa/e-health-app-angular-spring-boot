package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.NotAnImageFileException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.UserRepository;
import com.backend.ehealthspringboot.repository.VisitorRepository;
import com.backend.ehealthspringboot.service.DoctorService;
import com.backend.ehealthspringboot.service.EmailService;
import com.backend.ehealthspringboot.service.LoginAttemptService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.JPG_EXTENSION;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_DOCTOR;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
public class DoctorServiceImpl implements DoctorService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private DoctorRepository doctorRepository;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    @Autowired
    public DoctorServiceImpl(
            UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
    this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.doctorRepository = doctorRepository;

    }
    @Override
    public Doctor register(Doctor doctor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, doctor.getUsername(), doctor.getEmail());
        doctor.setUserId(generateUserId());
        String password = generatePassword();
        doctor.setJoinDate(new Date());
        doctor.setPassword(encodePassword(password));
        doctor.setActive(true);
        doctor.setNotLocked(true);
//        emailService.sendNewPasswordEmail(firstName, password, email);
//        set a default image for doctor
        LOGGER.info("New user password: " + password);
        doctor.setRole(ROLE_DOCTOR.name());
        doctor.setAuthorities(ROLE_DOCTOR.getAuthorities());
        doctor.setProfileImageUrl(getTemporaryProfileImageUrl(doctor.getGender()));
        doctorRepository.save(doctor);
        return doctor;
    }

    @Override
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor updateDoctor(String doctorUsername, Doctor doctor) throws UserNotFoundException, EmailExistException, UsernameExistException {
        Doctor newDoctor = validateNewUsernameAndEmail(doctorUsername,doctor.getUsername(),doctor.getEmail());
        newDoctor.setFirstName(doctor.getFirstName());
        newDoctor.setLastName(doctor.getLastName());
        newDoctor.setUsername(doctor.getUsername());
        newDoctor.setEmail(doctor.getEmail());
        newDoctor.setAddress(doctor.getAddress());
        newDoctor.setPhoneNumber(doctor.getPhoneNumber());
        newDoctor.setSpeciality(doctor.getSpeciality());
        newDoctor.setGender(doctor.getGender());
        doctorRepository.save(newDoctor);
        return newDoctor;
    }

    @Override
    public List<Doctor> findDoctorsByAllParameters(Doctor doctor){
        return doctorRepository.findBySpecialityNameAndAddressCountryAndAddressStateAndAddressCity
                (doctor.getSpeciality().getName(),
                        doctor.getAddress().getCountry(),
                        doctor.getAddress().getState(),
                        doctor.getAddress().getCity());
    }

    @Override
    public Doctor findDoctorByUsername(String username) {
        return doctorRepository.findDoctorByUsername(username);
    }


    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        User user = validateNewUsernameAndEmail(username, null, null);
        Doctor doctor = doctorRepository.findDoctorByUsername(username);
        saveProfileImage(doctor, profileImage);
        return doctor;
    }

    private Doctor validateNewUsernameAndEmail(String currentDoctorUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
//      updating case
        if(StringUtils.isNotBlank(currentDoctorUsername)) {
//            get current user info from DB
            Doctor currentDoctor = findDoctorByUsername(currentDoctorUsername);
            if(currentDoctor == null) {
                throw new UserNotFoundException(NO_DOCTOR_FOUND_BY_USERNAME + currentDoctorUsername);
            }
            if(userByNewUsername != null && !currentDoctor.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentDoctor.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentDoctor;
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

    private void saveProfileImage(Doctor doctor , MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }

            Path userFolder = Paths.get(USER_FOLDER + doctor.getUsername()).toAbsolutePath().normalize();
//            if the user directory does not exist
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + doctor.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(doctor.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            doctor.setProfileImageUrl(setProfileImageUrl(doctor.getUsername()));
            userRepository.save(doctor);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
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

    private String getTemporaryProfileImageUrl(String gender) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + gender).toUriString();
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}
