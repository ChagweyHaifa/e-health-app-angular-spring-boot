package com.backend.ehealthspringboot.repository;


import com.backend.ehealthspringboot.domain.Doctor;
import com.backend.ehealthspringboot.domain.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface VisitorRepository extends JpaRepository<Visitor,Long> {
    Visitor findVisitorByUsername(String username);
}
