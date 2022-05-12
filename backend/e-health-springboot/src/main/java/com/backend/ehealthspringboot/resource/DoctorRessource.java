package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.DoctorRating;
import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.domain.UserPrincipal;
import com.backend.ehealthspringboot.dto.DoctorDto;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.NotAnImageFileException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;
import com.backend.ehealthspringboot.service.DoctorService;
import com.backend.ehealthspringboot.service.UserService;
import com.backend.ehealthspringboot.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.FORWARD_SLASH;
import static com.backend.ehealthspringboot.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.backend.ehealthspringboot.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = "/api/users/doctors")
public class DoctorRessource extends ExceptionHandling {

    private DoctorService doctorService;
    private JWTTokenProvider jwtTokenProvider;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public DoctorRessource(DoctorService doctorService, JWTTokenProvider jwtTokenProvider ) {
       this.doctorService = doctorService;
       this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<Doctor> register(@RequestBody Doctor doctor ) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        Doctor newDoctor = doctorService.register(doctor);
        return new ResponseEntity<>(newDoctor, OK);
    }

    @GetMapping("")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return new ResponseEntity(doctors, OK);
    }

//    @PutMapping("")
//    @PreAuthorize("hasAnyAuthority('doctor:update')")
//    public ResponseEntity<Doctor> updateDoctor(HttpServletRequest request,@RequestBody Doctor theDoctor) throws UserNotFoundException, EmailExistException, UsernameExistException {
//        String loggedInDoctorUsername = getUsernameFromJWTToken(request);
//        Doctor doctor = doctorService.updateDoctor(loggedInDoctorUsername, theDoctor);
//        UserPrincipal userPrincipal = new UserPrincipal(doctor);
//        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
//        return new ResponseEntity(doctor, jwtHeader, OK);
//    }

    @PutMapping("")
    @PreAuthorize("hasAnyAuthority('doctor:update')")
    public ResponseEntity<Doctor> updateDoctor(HttpServletRequest request,@RequestBody DoctorDto doctorDto) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        String loggedInUsername = getUsernameFromJWTToken(request);
        Doctor doctor = doctorService.updateDoctor(loggedInUsername, doctorDto);
        UserPrincipal userPrincipal = new UserPrincipal(doctor);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity(doctor, jwtHeader, OK);
    }

    @GetMapping("/search/findByUsername/{username}")
    public ResponseEntity<Doctor> findDoctorByUsername(@PathVariable("username") String username) {
        Doctor doctor = doctorService.findDoctorByUsername(username);
        return new ResponseEntity(doctor, OK);
    }

    @PostMapping("/search/findByAllParameters")
    public ResponseEntity<List<Doctor>> findDoctorsByAllParameters(@RequestBody Doctor doctor){
        List<Doctor> doctors = doctorService.findDoctorsByAllParameters(doctor);
        return new ResponseEntity(doctors, OK);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Doctor> updateProfileImage(HttpServletRequest request, @RequestParam("profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        String loggedInDoctorUsername = getUsernameFromJWTToken(request);
        LOGGER.info("loggedInUsername"+ loggedInDoctorUsername);
        LOGGER.info("profileImage" + profileImage);
        Doctor doctor = doctorService.updateProfileImage(loggedInDoctorUsername, profileImage);
        return new ResponseEntity<>(doctor, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/default/{gender}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("gender") String gender) throws IOException {
        return Files.readAllBytes(Paths.get(TEMP_PROFILE_IMAGE_BASE_URL  + FORWARD_SLASH + gender + ".jpg"));
    }

    private String getUsernameFromJWTToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return jwtTokenProvider.getSubject(token);
    }
    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }
}
