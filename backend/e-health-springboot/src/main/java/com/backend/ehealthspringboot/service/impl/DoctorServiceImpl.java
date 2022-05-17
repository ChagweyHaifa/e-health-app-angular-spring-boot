package com.backend.ehealthspringboot.service.impl;
import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.Speciality;
import com.backend.ehealthspringboot.domain.User;

import com.backend.ehealthspringboot.enumeration.Role;
import com.backend.ehealthspringboot.enumeration.Status;
import com.backend.ehealthspringboot.exception.domain.*;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.SpecialityRepository;
import com.backend.ehealthspringboot.repository.UserRepository;
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
import java.util.Objects;

import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.JPG_EXTENSION;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_ADMIN;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_DOCTOR;
import static com.backend.ehealthspringboot.enumeration.Status.UNDER_VERIFICATION;
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
    private SpecialityRepository specialityRepository;


    @Autowired
    public DoctorServiceImpl(
            UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService,
            SpecialityRepository specialityRepository
          ) {
    this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.doctorRepository = doctorRepository;
        this.specialityRepository = specialityRepository;


    }

    @Override
    public Doctor register(Doctor doctor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, doctor.getUsername(), doctor.getEmail());
        doctor.setUserId(generateUserId());
        String password = generatePassword();
        doctor.setJoinDate(new Date());
        doctor.setPassword(encodePassword(password));
        doctor.setActive(true);
        doctor.setStatus(UNDER_VERIFICATION.name());
        doctor.setNotLocked(false);
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
    public Doctor findDoctorByUsername(String username) {
        return doctorRepository.findDoctorByUsername(username);
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
    public Doctor updateDoctor(String loggedInUsername,String currentDoctorUsername, Doctor theDoctor) throws Exception {
        User user = userRepository.findUserByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        LOGGER.info("loggedInUsername: " + loggedInUsername);
        LOGGER.info("currentDoctorUsername: " + currentDoctorUsername);
        LOGGER.info("new doctor username: " + theDoctor.getUsername());
        LOGGER.info(String.valueOf(getRoleEnumName(user.getRole()) == ROLE_DOCTOR));
        LOGGER.info(String.valueOf(getRoleEnumName(user.getRole()) == ROLE_ADMIN));
        LOGGER.info(String.valueOf(Objects.isNull(currentDoctorUsername)));
        Doctor newDoctor;
        if((getRoleEnumName(user.getRole()) == ROLE_ADMIN )){
            newDoctor = validateNewUsernameAndEmail(currentDoctorUsername ,theDoctor.getUsername(),theDoctor.getEmail());
            LOGGER.info("admin");
            newDoctor.setStatus( getStatusEnumName(theDoctor.getStatus()).name());
            newDoctor.setRole(getRoleEnumName(theDoctor.getRole()).name());
        }else {
            LOGGER.info("doctor");
            newDoctor = validateNewUsernameAndEmail(loggedInUsername ,theDoctor.getUsername(),theDoctor.getEmail());
        }

        newDoctor.setFirstName(theDoctor.getFirstName());
        newDoctor.setLastName(theDoctor.getLastName());
        newDoctor.setUsername(theDoctor.getUsername());
        newDoctor.setEmail(theDoctor.getEmail());
        newDoctor.setAddress(theDoctor.getAddress());
        newDoctor.setPhoneNumber(theDoctor.getPhoneNumber());
        if(theDoctor.getPassword() != null){
            newDoctor.setPassword(encodePassword(theDoctor.getPassword()));
        }
        Speciality speciality = specialityRepository.findByName(theDoctor.getSpeciality().getName());
        if (speciality == null){
            throw new SpecialityNotFoundException(NO_SPECIALTY_FOUND);
        }
        newDoctor.setSpeciality(speciality);
        newDoctor.setNotLocked(theDoctor.isNotLocked());
        newDoctor.setActive(theDoctor.isActive());
        newDoctor.setGender(theDoctor.getGender());
        doctorRepository.save(newDoctor);


        return newDoctor;
    }

    @Override
    public Doctor updateProfileImage(String doctorUsername, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        Doctor doctor = validateNewUsernameAndEmail(doctorUsername, null, null);
        saveProfileImage(doctor, profileImage);
        return doctor;
    }



    private Doctor validateNewUsernameAndEmail(String currentDoctorUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
//      updating case
        if(StringUtils.isNotBlank(currentDoctorUsername)) {
//            check if the doctor exists
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
            Path userFolder = Paths.get(USER_FOLDER + FORWARD_SLASH + doctor.getUsername()).toAbsolutePath().normalize();
//            if the user directory does not exist
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + doctor.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(doctor.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);

            doctor.setProfileImageUrl(setProfileImageUrl(doctor.getUsername()));
            doctorRepository.save(doctor);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String getTemporaryProfileImageUrl(String gender) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH+FORWARD_SLASH + gender).toUriString();
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + FORWARD_SLASH+ username + FORWARD_SLASH
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


    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
    private Status getStatusEnumName(String status) {
        return Status.valueOf(status.toUpperCase());
    }
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}
