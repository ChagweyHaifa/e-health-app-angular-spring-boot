package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ReviewRepository extends JpaRepository<Review,Long> {

}
