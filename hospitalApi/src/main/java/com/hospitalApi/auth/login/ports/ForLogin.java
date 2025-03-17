package com.hospitalApi.auth.login.ports;

import org.springframework.security.authentication.BadCredentialsException;

import com.hospitalApi.auth.login.dtos.LoginResponseDTO;
import com.hospitalApi.shared.exceptions.NotFoundException;

public interface ForLogin {
    /**
     * Autentica al usuario con sus credenciales.
     * 
     * @param username
     * @param password
     * @return
     * @throws NotFoundException
     * @throws BadCredentialsException
     */
    public LoginResponseDTO login(String username, String password) throws NotFoundException, BadCredentialsException;
}
