package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    Doctor findDoctorByUsername(String username);
}
