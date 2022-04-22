package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;
import com.backend.ehealthspringboot.repository.UserRepository;
import com.backend.ehealthspringboot.repository.VisitorRepository;
import com.backend.ehealthspringboot.service.EmailService;
import com.backend.ehealthspringboot.service.LoginAttemptService;
import com.backend.ehealthspringboot.service.VisitorService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_VISITOR;
import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
public class VisitorServiceImpl implements VisitorService {

        private Logger LOGGER = LoggerFactory.getLogger(getClass());
        private BCryptPasswordEncoder passwordEncoder;
        private UserRepository userRepository;
        private VisitorRepository visitorRepository;
        private LoginAttemptService loginAttemptService;
        private EmailService emailService;

    @Autowired
    public VisitorServiceImpl(
            UserRepository userRepository,
            VisitorRepository visitorRepository,
            BCryptPasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.visitorRepository = visitorRepository;

    }


    @Override
    public Visitor register(Visitor visitor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, visitor.getUsername(), visitor.getEmail());
        visitor.setUserId(generateUserId());
        String password = generatePassword();
        visitor.setJoinDate(new Date());
        visitor.setPassword(encodePassword(password));
        visitor.setActive(true);
        visitor.setNotLocked(true);
//        emailService.sendNewPasswordEmail(firstName, password, email);
//        set a default image for user
        LOGGER.info("New user password: " + password);
        visitor.setRole(ROLE_VISITOR.name());
        visitor.setAuthorities(ROLE_VISITOR.getAuthorities());
        visitorRepository.save(visitor);
        return visitor;
    }

    @Override
    public List<Visitor> getVisitors() {
        return visitorRepository.findAll();
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

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


}
