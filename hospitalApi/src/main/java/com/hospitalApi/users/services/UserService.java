package com.hospitalApi.users.services;

import org.springframework.stereotype.Service;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.utils.PasswordEncoderUtil;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;
import com.hospitalApi.users.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements ForUsersPort {

    private final UserRepository userRepository;
    private final PasswordEncoderUtil passwordEncoderUtil;

    @Transactional(rollbackOn = Exception.class)
    public User createUser(User newUser) throws DuplicatedEntryException {
        // verificamos si el nombre de usuario existe ya en l bd
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new DuplicatedEntryException(
                    "Ya existe un usuario con el mismo nombre de usuario.");
        }

        // mandamos a encriptar la password
        newUser.setPassword(passwordEncoderUtil.encode(newUser.getPassword()));

        // guardar el usuario
        User save = userRepository.save(newUser);

        return save;
    }

}
