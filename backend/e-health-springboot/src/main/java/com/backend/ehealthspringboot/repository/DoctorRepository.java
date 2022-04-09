package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


//@RepositoryRestResource(exported = false)
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Doctor findDoctorByUsername(String username);
    List<Doctor> findBySpecialityNameAndAddressState(String specialityName,String AddressName);
}
