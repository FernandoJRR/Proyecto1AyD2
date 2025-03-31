package com.hospitalApi.auth.login.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.hospitalApi.auth.jwt.ports.ForJwtGenerator;
import com.hospitalApi.auth.login.dtos.LoginResponseDTO;
import com.hospitalApi.auth.login.ports.ForLogin;
import com.hospitalApi.auth.login.ports.ForUserLoader;
import com.hospitalApi.employees.dtos.EmployeeResponseDTO;
import com.hospitalApi.employees.mappers.EmployeeMapper;
import com.hospitalApi.permissions.dtos.PermissionResponseDTO;
import com.hospitalApi.permissions.mappers.PermissionMapper;
import com.hospitalApi.shared.exceptions.NotFoundException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.ports.ForUsersPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class LoginService implements ForLogin {

    private final AuthenticationManager authenticationManager;
    private final ForJwtGenerator forJwtGenerator;
    private final ForUsersPort forUsersPort;
    private final ForUserLoader forUserLoader;

    // mappers
    private final EmployeeMapper employeeMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public LoginResponseDTO login(String username, String password) throws NotFoundException, BadCredentialsException {

        // traer el usuario por nombre de usuario
        User user = forUsersPort.findUserByUsername(username);

        // verificar que el usuario no esté desactivado
        if (user.getDesactivatedAt() != null) {
            throw new NotFoundException("El usuario se encuentra desactivado.");
        }

        // autenticamos el usuario con usuario y contrasenia
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // JWT

        // si la autenticacion no fallo entonces cargamos los permisos del usuario
        Set<GrantedAuthority> authorities = forUserLoader.loadUserPermissions(user);
        // cagados los permisos entonces generamos el jwt
        String jwt = forJwtGenerator.generateToken(user, authorities);
        // construimos la respuesta
        EmployeeResponseDTO employeeResponseDTO = employeeMapper.fromEmployeeToResponse(user.getEmployee());

        List<PermissionResponseDTO> permissions = permissionMapper
                .fromPermissionsToPermissionsReponseDtos(user.getEmployee().getEmployeeType().getPermissions());
        return new LoginResponseDTO(user.getUsername(), employeeResponseDTO, jwt, permissions);

    }

}
