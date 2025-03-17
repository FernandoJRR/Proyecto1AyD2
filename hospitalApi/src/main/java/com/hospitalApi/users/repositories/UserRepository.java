package com.hospitalApi.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.users.models.User;

public interface UserRepository extends JpaRepository<User, String> {

    public Boolean existsByUsername(String username);

    public Optional<User> findByUsername(String usernamme);
}
