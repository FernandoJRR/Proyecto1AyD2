package com.hospitalApi.usuarios.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.users.models.User;
import com.hospitalApi.users.repositories.UserRepository;
import com.hospitalApi.users.services.UserService;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
    public void createUserWithExistingUsername() {
        // configuramos el mock para que lance una excepción al intentar crearlo
        when(userRepository.existByUsername(user.getUsername())).thenReturn(true);

        // verificamos que la excepción se lanza correctamente
        assertThrows(DuplicatedEntryException.class, () -> {
            userService.createUser(user);
        });

        // verificamos que el metodo save no se haya ejecutado
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void createUser() {
        // configuramos el mock para que lance false al hacerse la veriicacion de
        // existencia
        when(userRepository.existByUsername(user.getUsername())).thenReturn(false);
        // verificamos que el metodo save se haya ejecutado
        verify(userRepository, times(1)).save(user);
    }
}
