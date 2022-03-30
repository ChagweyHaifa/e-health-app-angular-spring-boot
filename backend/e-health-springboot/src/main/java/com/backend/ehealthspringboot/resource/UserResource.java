package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.service.UserService;
import com.backend.ehealthspringboot.utility.JWTTokenProvider;

import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.*;
import static com.backend.ehealthspringboot.constant.SecurityConstant.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping(path= {"/","/api"})
// it will look for a handler for this exception in ExceptionHandling class
public class UserResource extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private UserService userService;
	private AuthenticationManager authenticationManager;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	public UserResource(AuthenticationManager authenticationManager, UserService userService, JWTTokenProvider jwtTokenProvider) {
	    this.authenticationManager = authenticationManager;
	    this.userService = userService;
	    this.jwtTokenProvider = jwtTokenProvider;
	}

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }


    @GetMapping("/users/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = userService.getDoctors();
        return new ResponseEntity(doctors, OK);
    }

    @GetMapping("/users/visitors")
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> visitors = userService.getVisitors();
        return new ResponseEntity<>(visitors, OK);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = userService.getStudents();
        return new ResponseEntity<>(students, OK);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = userService.getCourses();
        return new ResponseEntity<>(courses, OK);
    }

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
	    authenticate(user.getUsername(), user.getPassword());
	    User loginUser = userService.findUserByUsername(user.getUsername());
	    UserPrincipal userPrincipal = new UserPrincipal(loginUser);
	    HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
	    return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

	@PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

//    @PostMapping("/users")
//    @PreAuthorize("hasAnyAuthority('user:create')")
//    //   => only admin and super_admin are authorized
//    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
//                                           @RequestParam("lastName") String lastName,
//                                           @RequestParam("username") String username,
//                                           @RequestParam("email") String email,
//                                           @RequestParam("role") String role,
//                                           @RequestParam("isActive") String isActive,
//                                           @RequestParam("isNonLocked") String isNonLocked,
//                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
//            throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
//        User newUser = userService.addNewUser(firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
//        return new ResponseEntity<>(newUser, OK);
//    }

//    @PostMapping("/add")
//    public ResponseEntity<User> addNewUser(@RequestBody User newUser)
//            throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
//        User User = userService.addNewUser(newUser.getFirstName(), newUser.getLastName(), newUser.getUsername(),newUser.getEmail(), newUser.getRole(), newUser.isNotLocked(), newUser.isActive(), newUser.getProfileImageUrl());
//        return new ResponseEntity<>(newUser, OK);
//    }

    @PutMapping("/users")
//    @PreAuthorize("hasAnyAuthority('user:update')")
    //   => only manager, admin and super_admin are authorized
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNonLocked") String isNonLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username,email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
//                 ResponseEntity<>(User object, HttpStatus object)
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
//  only super_admin is authorized
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
//                     (HttpStatusObject,message)
//           = ResponseEntity<>(HttpResponseObject, HttpStatusObject)
//           = ResponseEntity<>((HttpStatusObject ,message,..), HttpStatusObject)
    }



    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
//
    }
    
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
//  private methods
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

	private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

	private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }


}
