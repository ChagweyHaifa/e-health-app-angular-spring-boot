package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country,Long> {
}
