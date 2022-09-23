package com.backend.ehealthspringboot.service;

import com.backend.ehealthspringboot.domain.Question;
import com.backend.ehealthspringboot.domain.QuestionResponse;
import com.backend.ehealthspringboot.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {


    Question[] findQuestionsBySpecialityName(String specialityName);

    Question addQuestion(String loggedInUsername, String specialityName, String questionTitle, String question, String currentTreatment, String medicalHistory, Integer height, Integer weight, List<MultipartFile> attachements) throws SpecialityNotFoundException, UserNotFoundException;


    Question editQuestion(String loggedInUsername, Long questionId, String specialityName, String questionTitle, String question, String currentTreatment, String medicalHistory, Integer height, Integer weight, List<MultipartFile> attachements) throws Exception;

    void deleteQuestion(String loggedInUsername, Long questionId) throws Exception;

    Question addResponse(String loggedInUsername, Long questionId, QuestionResponse questionResponse) throws UserNotFoundException, QuestionNotFoundException, QuestionResponseExistExecption;

    Question updateResponse(String loggedInUsername, Long questionId, QuestionResponse questionResponse) throws Exception;

    Question deleteResponse(String loggedInUsername, Long questionId) throws Exception;
}
