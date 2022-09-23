package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.DoctorRating;
import com.backend.ehealthspringboot.domain.DoctorRatingKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating,Long> {

    List<DoctorRating> findByDoctorUsername(String username);




    DoctorRating findByUserUsernameAndDoctorUsername(String loggedInUsername, String doctorUsername);
}
