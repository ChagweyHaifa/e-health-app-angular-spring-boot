package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;

import javax.mail.MessagingException;
import java.util.List;

public interface VisitorService {

    List<Visitor> getVisitors();

    Visitor register(Visitor visitor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

}
