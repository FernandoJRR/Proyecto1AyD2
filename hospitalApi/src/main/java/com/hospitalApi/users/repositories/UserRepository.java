package com.hospitalApi.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospitalApi.users.models.User;

public interface UserRepository extends JpaRepository<User, String> {

    public Boolean existByUsername(String username);
}
