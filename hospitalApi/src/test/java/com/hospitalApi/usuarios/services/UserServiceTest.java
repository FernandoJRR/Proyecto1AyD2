package com.hospitalApi.usuarios.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.utils.PasswordEncoderUtil;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.repositories.UserRepository;
import com.hospitalApi.users.services.UserService;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoderUtil passwordEncoderUtil;

    @InjectMocks
    private UserService userService;

    private User user;

    /**
     * este metodo se ejecuta antes de cualquier prueba individual, se hace para
     * inicializar los moks
     */
    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("Luis", "123");
    }

    @Test
    public void createUser() {
        try {
            // ARRAGE
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
            when(passwordEncoderUtil.encode(anyString())).thenReturn("criptedPassword");
            when(userRepository.save(user)).thenReturn(user);
            // ACT
            User result = userService.createUser(user);

            // ASSERT
            assertAll(
                    () -> assertEquals("criptedPassword", result.getPassword(),
                            "La password debe estar encriptada encriptada."));

            verify(userRepository, times(1)).save(user);
        } catch (DuplicatedEntryException e) {

        }

    }

    @Test
    public void createUserWithExistingUsername() {
        // configuramos el mock para que lance una excepción al intentar crearlo
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // verificamos que la excepción se lanza correctamente
        assertThrows(DuplicatedEntryException.class, () -> {
            userService.createUser(user);
        });

        // verificamos que el metodo save no se haya ejecutado
        verify(userRepository, times(0)).save(any(User.class));
    }

}
