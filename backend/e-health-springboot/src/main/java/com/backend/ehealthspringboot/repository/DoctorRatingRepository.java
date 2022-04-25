package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating,Long> {
}
