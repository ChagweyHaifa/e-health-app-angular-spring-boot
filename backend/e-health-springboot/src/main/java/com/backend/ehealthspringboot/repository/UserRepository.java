package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import com.backend.ehealthspringboot.domain.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

//    @Query("from Doctor")
//    List<Doctor> findAllDoctors();
//
//    @Query("from Doctor")
//    Doctor findDoctorByUsername(String doctorUsername);
//
//    @Query("from Visitor")
//    List<Visitor> findAllVisitors();
}
