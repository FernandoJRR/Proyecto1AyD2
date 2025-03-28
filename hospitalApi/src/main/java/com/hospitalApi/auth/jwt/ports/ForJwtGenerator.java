package com.hospitalApi.auth.jwt.ports;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.hospitalApi.users.models.User;

public interface ForJwtGenerator {
    /**
     * Genera un token jwt con los permisos del usuario
     * @param user
     * @param permissions
     * @return
     */
    public String generateToken(User user, Set<GrantedAuthority> permissions);
}
