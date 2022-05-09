package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


public interface SpecialityRepository extends JpaRepository< Speciality,Long> {

    Speciality findByName(String name);
}
