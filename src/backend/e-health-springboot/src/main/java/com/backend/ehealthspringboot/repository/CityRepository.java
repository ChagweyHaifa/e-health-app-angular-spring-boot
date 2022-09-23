package com.backend.ehealthspringboot.repository;
import com.backend.ehealthspringboot.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface CityRepository extends JpaRepository<City,Long> {
//  /cities/search/findByStateName?name=Tunis&sort=name,asc
    Page<City> findByStateName(@RequestParam("name") String name, Pageable pageable);
}
