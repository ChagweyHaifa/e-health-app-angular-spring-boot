package com.backend.ehealthspringboot.resource;
import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static com.backend.ehealthspringboot.constant.SecurityConstant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	public UserResource(AuthenticationManager authenticationManager, UserService userService, JWTTokenProvider jwtTokenProvider) {
	    this.authenticationManager = authenticationManager;
	    this.userService = userService;
	    this.jwtTokenProvider = jwtTokenProvider;
	}

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
//        LOGGER.info("user" + user);
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        User newUser = userService.register(user);
        return new ResponseEntity<>(newUser, OK);
    }

//    @GetMapping("/users")
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = userService.getUsers();
//        return new ResponseEntity<>(users, OK);
//    }
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam("role")String role) {
        List<User> users = userService.getUsersByRole(role);
        return new ResponseEntity<>(users, OK);
    }

//    @PostMapping("/users")
//    public ResponseEntity<User> addNewUser(@RequestBody User user){
//        User newUser = userService.addNewUser(user);
//        return new ResponseEntity(newUser,OK);
//
//    }

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

    @PutMapping("/users/{currentUsername}")
//    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<User> update(HttpServletRequest request,@RequestBody User user,@PathVariable("currentUsername")String currentUsername) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        String loggedInUsername = getUsernameFromJWTToken(request);
        User updatedUser = userService.updateUser(loggedInUsername,currentUsername,user);
        UserPrincipal userPrincipal = new UserPrincipal(updatedUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(updatedUser,jwtHeader, OK);
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

//  private methods
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

	private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
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