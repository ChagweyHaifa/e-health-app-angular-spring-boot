package com.backend.ehealthspringboot.repository;

import com.backend.ehealthspringboot.domain.City;
import com.backend.ehealthspringboot.domain.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface StateRepository extends JpaRepository<State,Long> {
    Page<State> findByCountryName(@RequestParam("name") String name, Pageable pageable);


}
