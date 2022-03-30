package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
