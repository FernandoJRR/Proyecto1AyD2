package com.hospitalApi.auth.login.ports;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.hospitalApi.users.models.User;

public interface ForUserLoader extends UserDetailsService {
    /**
     * Metodo usado para cargar los permisos del usuario al jwt o al contexto de
     * seguridad de spring
     * 
     * @param user
     * @return
     */
    public Set<GrantedAuthority> loadUserPermissions(User user);
}
