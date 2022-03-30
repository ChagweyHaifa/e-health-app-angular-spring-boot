package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Course;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
