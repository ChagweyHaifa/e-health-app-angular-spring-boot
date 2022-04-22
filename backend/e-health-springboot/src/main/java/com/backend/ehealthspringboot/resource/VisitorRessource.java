package com.backend.ehealthspringboot.resource;

import com.backend.ehealthspringboot.domain.Visitor;
import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.exception.domain.EmailExistException;
import com.backend.ehealthspringboot.exception.domain.UserNotFoundException;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;
import com.backend.ehealthspringboot.service.DoctorService;
import com.backend.ehealthspringboot.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/users/visitors")
public class VisitorRessource extends ExceptionHandling {

    private VisitorService visitorService;

    @Autowired
    public VisitorRessource(VisitorService visitorService ) {
        this.visitorService = visitorService;
    }

    @GetMapping("")
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> visitors = visitorService.getVisitors();
        return new ResponseEntity<>(visitors, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Visitor> registerVisitor(@RequestBody Visitor visitor) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
        Visitor newVisitor = visitorService.register(visitor);
        return new ResponseEntity<>(newVisitor, OK);
    }
}
