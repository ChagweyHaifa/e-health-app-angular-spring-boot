package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {

    Question[] findByTitleContaining(String name);

    Question[] findBySpecialityName(String specialityName);

    Question findQuestionById(Long questionId);

    Question findQuestionByIdAndUserUsername(Long questionId, String loggedInUsername);
}
