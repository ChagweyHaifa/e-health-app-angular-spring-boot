package com.backend.ehealthspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.ehealthspringboot.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
