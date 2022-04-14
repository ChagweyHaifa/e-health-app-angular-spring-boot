package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


//@RepositoryRestResource(exported = false)
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Doctor findDoctorByUsername(String username);
//    @Query("SELECT c FROM Doctor c WHERE (:specialityName is null or :specialityName is empty or c.speciality.name = :specialityName) and (:addressState is null "
//            + "or :addressState is empty or c.address.state = :addressState)")

    @Query("SELECT c FROM Doctor c WHERE (:specialityName = '' or :specialityName is null or c.speciality.name = :specialityName) and " +
            "(:addressCountry = '' or :addressCountry is null or c.address.country = :addressCountry) and " +
            "(:addressState = '' or :addressState is null or c.address.state = :addressState) and " +
            "(:addressCity = '' or :addressCity is null or c.address.city = :addressCity)")
    List<Doctor> findBySpecialityNameAndAddressCountryAndAddressStateAndAddressCity(
            String specialityName,  String addressCountry,  String addressState, String addressCity);

}
