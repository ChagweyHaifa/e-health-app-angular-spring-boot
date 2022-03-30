package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
