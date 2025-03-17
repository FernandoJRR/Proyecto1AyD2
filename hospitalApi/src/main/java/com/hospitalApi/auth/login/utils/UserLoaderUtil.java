/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hospitalApi.auth.login.utils;

import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.hospitalApi.auth.login.ports.ForUserLoader;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;

/**
 *
 * @author Luis Monterroso
 */
@Component
public class UserLoaderUtil implements ForUserLoader {

    private final ForUsersPort forUsersPort;

    public UserLoaderUtil(@Lazy ForUsersPort forUsersPort) {
        this.forUsersPort = forUsersPort;
    }

    /**
     * Metodo usado para que Spring pueda cargar el usuario en el contexto de
     * seguridad.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // traer el usuario por nombre de usuario
            User user = forUsersPort.findUserByUsername(username);

            // cargamos sus roles y permisos
            Set<GrantedAuthority> permissions = loadUserPermissions(user);

            return new org.springframework.security.core.userdetails.User(username,
                    user.getPassword(),
                    permissions);
        } catch (NotFoundException e) { // si hubo not found entonces devolvemos el erro especifico para el usuario
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    public Set<GrantedAuthority> loadUserPermissions(User user) {
        return Set.of();
    }
}
