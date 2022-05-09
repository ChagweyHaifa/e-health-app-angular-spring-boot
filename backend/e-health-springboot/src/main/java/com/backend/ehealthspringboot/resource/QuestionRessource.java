package com.backend.ehealthspringboot.resource;


import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import com.backend.ehealthspringboot.service.QuestionService;
import com.backend.ehealthspringboot.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

import static com.backend.ehealthspringboot.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/api/questions")
public class QuestionRessource {

    public static final String QUESTION_DELETED_SUCCESSFULLY = "Question deleted successfully";
    private QuestionService questionService ;
    private JWTTokenProvider jwtTokenProvider;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public QuestionRessource(QuestionService questionService, JWTTokenProvider jwtTokenProvider){
        this.questionService = questionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/{specialityName}")
    public ResponseEntity<Question[]> findQuestionsBySpecialityName(@PathVariable("specialityName") String specialityName){
        Question[] questions = questionService.findQuestionsBySpecialityName(specialityName);
        return new ResponseEntity<>(questions, OK);
    }


    @PostMapping("")
    public ResponseEntity<Question> addQuestion(HttpServletRequest request , @RequestParam("specialityName") String specialityName,
                                            @RequestParam("questionTitle") String questionTitle,
                                            @RequestParam("question") String question,
                                            @RequestParam(value = "height",required = false) Integer height,
                                              @RequestParam(value = "weight",required =false) Integer weight,
                                                @RequestParam(value = "currentTreatment",required = false) String currentTreatment,
                                                @RequestParam(value = "medicalHistory",required = false) String medicalHistory,
                                            @RequestParam(value = "attachements",required = false) List<MultipartFile> attachements) throws UserNotFoundException, SpecialityNotFoundException {
        if( height == null){
            height = 0;
        }
        if(weight == null){
            weight  = 0;
        }
        String loggedInUsername = getUsernameFromJWTToken(request);
       Question theQuestion = questionService.addQuestion(loggedInUsername,specialityName, questionTitle, question, currentTreatment, medicalHistory, height,weight, attachements);
        return new ResponseEntity<>(theQuestion, OK);
    }

    @PutMapping("")
    public ResponseEntity<Question> editQuestion(HttpServletRequest request ,
                                                 @RequestParam("questionId") Long questionId,
                                                 @RequestParam("specialityName") String specialityName,
                                                @RequestParam("questionTitle") String questionTitle,
                                                @RequestParam("question") String question,
                                                @RequestParam(value = "height",required = false) Integer height,
                                                @RequestParam(value = "weight",required =false) Integer weight,
                                                @RequestParam(value = "currentTreatment",required = false) String currentTreatment,
                                                @RequestParam(value = "medicalHistory",required = false) String medicalHistory,
                                                @RequestParam(value = "attachements",required = false) List<MultipartFile> attachements) throws Exception {
        if( height == null){
            height = 0;
        }
        if(weight == null){
            weight  = 0;
        }
        String loggedInUsername = getUsernameFromJWTToken(request);
        Question theQuestion = questionService.editQuestion(loggedInUsername, questionId, specialityName, questionTitle, question, currentTreatment, medicalHistory, height,weight, attachements);
        return new ResponseEntity<>(theQuestion, OK);

    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<HttpResponse> deleteQuestion(HttpServletRequest request , @PathVariable("questionId") Long questionId) throws Exception {
        String loggedInUsername = getUsernameFromJWTToken(request);
        questionService.deleteQuestion(loggedInUsername,questionId);
        return response(OK, QUESTION_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/responses/{questionId}")
    public ResponseEntity<Question> addResponse(HttpServletRequest request, @PathVariable("questionId") Long questionId ,@RequestBody QuestionResponse questionResponse) throws UserNotFoundException, QuestionNotFoundException, QuestionResponseExistExecption {
        String loggedInUsername = getUsernameFromJWTToken(request);
        Question question = questionService.addResponse(loggedInUsername,questionId,questionResponse);
        return new ResponseEntity<>(question, OK);
    }

    @PutMapping("/responses/{questionId}")
    @PreAuthorize("hasAnyAuthority('questionResponse:update')")
    public ResponseEntity<Question> updateResponse(HttpServletRequest request, @PathVariable("questionId") Long questionId ,@RequestBody QuestionResponse questionResponse) throws Exception {
        String loggedInUsername = getUsernameFromJWTToken(request);
        Question question = questionService.updateResponse(loggedInUsername,questionId,questionResponse);
        return new ResponseEntity<>(question, OK);
    }

    @DeleteMapping("/responses/{questionId}")
    @PreAuthorize("hasAnyAuthority('questionResponse:delete')")
    public ResponseEntity<Question> updateResponse(HttpServletRequest request, @PathVariable("questionId") Long questionId) throws Exception {
        String loggedInUsername = getUsernameFromJWTToken(request);
        Question question = questionService.deleteResponse(loggedInUsername,questionId);
        return new ResponseEntity<>(question, OK);
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    private String getUsernameFromJWTToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return   jwtTokenProvider.getSubject(token);
    }
}
