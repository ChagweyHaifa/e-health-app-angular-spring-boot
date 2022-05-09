package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.*;
import com.backend.ehealthspringboot.exception.domain.*;
import com.backend.ehealthspringboot.repository.DoctorRepository;
import com.backend.ehealthspringboot.repository.QuestionRepository;
import com.backend.ehealthspringboot.repository.SpecialityRepository;
import com.backend.ehealthspringboot.repository.UserRepository;
import com.backend.ehealthspringboot.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.backend.ehealthspringboot.constant.FileConstant.*;
import static com.backend.ehealthspringboot.constant.FileConstant.JPG_EXTENSION;
import static com.backend.ehealthspringboot.constant.UserImplConstant.*;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_ADMIN;
import static com.backend.ehealthspringboot.enumeration.Role.ROLE_DOCTOR;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class QuestionServiceImpl implements QuestionService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private DoctorRepository doctorRepository;
    private QuestionRepository questionRepository;
    private SpecialityRepository specialityRepository;

    @Autowired
    public  QuestionServiceImpl(UserRepository userRepository,QuestionRepository questionRepository,SpecialityRepository specialityRepository, DoctorRepository doctorRepository){
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.questionRepository= questionRepository;
        this.specialityRepository = specialityRepository;
    }

    @Override
    public Question addQuestion(String loggedInUsername,String specialityName,  String questionTitle, String question,String currentTreatment,String medicalHistory ,Integer height, Integer weight, List<MultipartFile> attachements) throws SpecialityNotFoundException, UserNotFoundException {
        User user = userRepository.findUserByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        Speciality speciality = specialityRepository.findByName(specialityName);
        if (speciality == null){
            throw new SpecialityNotFoundException(NO_SPECIALTY_FOUND);
        }
//        LOGGER.info("loggedInUser"+ loggedInUsername);
        Question myQuestion = new Question();
        myQuestion.setSpeciality(speciality);
        myQuestion.setUser(user);
        myQuestion.setTitle(questionTitle);
        myQuestion.setQuestion(question);
        myQuestion.setCurrentTreatment(currentTreatment);
        myQuestion.setMedicalHistory(medicalHistory);
        myQuestion.setQuestionerHeight(height);
        myQuestion.setQuestionerWeight(weight);
        questionRepository.save(myQuestion);
//        Path userFolder = Paths.get(USER_FOLDER ).toAbsolutePath().normalize();
//        Files.copy(attachements.get(0).getInputStream(), userFolder.resolve("file" + DOT + JPG_EXTENSION), REPLACE_EXISTING);
//        Files.copy(attachements.get(1).getInputStream(), userFolder.resolve("file2" + DOT + JPG_EXTENSION), REPLACE_EXISTING);
        return myQuestion;
    }


    @Override
    public Question editQuestion(String loggedInUsername, Long questionId, String specialityName, String questionTitle, String question, String currentTreatment, String medicalHistory, Integer height, Integer weight, List<MultipartFile> attachements) throws Exception {
        User user = userRepository.findUserByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        Question theQuestion = questionRepository.findQuestionById(questionId);
        if (theQuestion == null){
            throw new QuestionNotFoundException(NO_QUESTION_FOUND);
        }
        if (user.getRole() != ROLE_ADMIN.name() ) {
            if (theQuestion.getUser().getUsername() != user.getUsername()) {
                throw new Exception("You don't have permission to edit this question");
            }
        }
        Speciality speciality = specialityRepository.findByName(specialityName);
        if (speciality == null){
            throw new SpecialityNotFoundException(NO_SPECIALTY_FOUND);
        }
        theQuestion.setSpeciality(speciality);
        theQuestion.setTitle(questionTitle);
        theQuestion.setQuestion(question);
        theQuestion.setCurrentTreatment(currentTreatment);
        theQuestion.setMedicalHistory(medicalHistory);
        theQuestion.setQuestionerHeight(height);
        theQuestion.setQuestionerWeight(weight);
        questionRepository.save(theQuestion);
        return theQuestion;
    }

    @Override
    public void deleteQuestion(String loggedInUsername, Long questionId) throws Exception {
        User user = userRepository.findUserByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        Question theQuestion = questionRepository.findQuestionById(questionId);
        if (theQuestion == null){
            throw new QuestionNotFoundException(NO_QUESTION_FOUND);
        }
        if (user.getRole() != ROLE_ADMIN.name() ) {
            if (theQuestion.getUser().getUsername() != user.getUsername()) {
                throw new Exception("You don't have permission to delete this question");
            }
        }
        questionRepository.deleteById(theQuestion.getId());
    }

    @Override
    public Question addResponse(String loggedInUsername, Long questionId, QuestionResponse questionResponse) throws UserNotFoundException, QuestionNotFoundException, QuestionResponseExistExecption {
        Doctor doctor = doctorRepository.findDoctorByUsername(loggedInUsername);
        if(doctor == null){
            throw new UserNotFoundException(NO_DOCTOR_FOUND_BY_USERNAME + loggedInUsername);
        }
        Question theQuestion = questionRepository.findQuestionById(questionId);
        if (theQuestion == null){
            throw new QuestionNotFoundException(NO_QUESTION_FOUND);
        }
        if (theQuestion.getResponse() != null){
            throw new QuestionResponseExistExecption(RESPONSE_ALREADY_EXISTS);
        }
        questionResponse.setDoctor(doctor);
        theQuestion.setResponse(questionResponse);
        questionRepository.save(theQuestion);
        return theQuestion;
    }

    @Override
    public Question updateResponse(String loggedInUsername, Long questionId, QuestionResponse questionResponse) throws Exception {

        Question theQuestion = questionRepository.findQuestionById(questionId);
        if (theQuestion == null){
            throw new QuestionNotFoundException(NO_QUESTION_FOUND);
        }
        User user = userRepository.findByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        if (theQuestion.getResponse() == null){
                throw new QuestionResponseNotFound(QUESTION_RESPONSE_NOT_FOUND);
        }
        if (theQuestion.getResponse().getDoctor().getRole() == ROLE_DOCTOR.name()){
            if(theQuestion.getResponse().getDoctor().getUsername() != user.getUsername()){
                throw new Exception("You don't have permission to edit this response");
            }
        }
        theQuestion.getResponse().setContent(questionResponse.getContent());
        questionRepository.save(theQuestion);
        return theQuestion;
    }

    @Override
    public Question deleteResponse(String loggedInUsername, Long questionId) throws Exception {

        Question theQuestion = questionRepository.findQuestionById(questionId);
        if (theQuestion == null){
            throw new QuestionNotFoundException(NO_QUESTION_FOUND);
        }
        User user = userRepository.findByUsername(loggedInUsername);
        if(user == null){
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + loggedInUsername);
        }
        if (theQuestion.getResponse() == null){
            throw new QuestionResponseNotFound(QUESTION_RESPONSE_NOT_FOUND);
        }

        if (theQuestion.getResponse().getDoctor().getRole() == ROLE_DOCTOR.name()){
            if(theQuestion.getResponse().getDoctor().getUsername() != user.getUsername()){
                throw new Exception("You don't have permission to delete this response");
            }
        }
        theQuestion.setResponse(null);
        questionRepository.save(theQuestion);
        return theQuestion;
    }


    @Override
    public Question[] findQuestionsBySpecialityName(String specialityName) {
        Question[]  questions= questionRepository.findBySpecialityName(specialityName);
        return  questions;
    }
}

