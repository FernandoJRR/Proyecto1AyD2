package com.hospitalApi.auth.jwt.filters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hospitalApi.auth.jwt.utils.JwtManagerUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtManagerUtil jwtManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String JWT = "dsa.asd.asd";
    private static final String USERNAME = "userMock";
    private static final String PASSWORD = "passSecret";

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        userDetails = new User(USERNAME, PASSWORD, false, false, false, false, Set.of());
    }

    /**
     * dado: un request con un token válido en el header Authorization.
     * cuando: el filtro intercepta la solicitud.
     * entonces: el usuario es autenticado y el filtro continúa la cadena.
     */
    @Test
    public void doFilterInternalShouldAuthenticateWhenTokenIsValid() throws Exception {
        // arrange
        // simulamos la extraccion corecta del header del token
        when(request.getHeader(anyString())).thenReturn("Bearer " + JWT);
        // simular la extraccion del usuario
        when(jwtManager.extractUsername(anyString())).thenReturn(USERNAME);
        // cuando se haga el laod entonces devoleremos nuestro mock
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        // cuando se trate de verificar el token entonces devolemos true en senial de
        // que es valido
        when(jwtManager.isTokenValid(anyString(), anyString())).thenReturn(true);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // asset
        verify(jwtManager, times(1)).extractUsername(JWT);
        verify(userDetailsService, times(1)).loadUserByUsername(USERNAME);
        verify(jwtManager, times(1)).isTokenValid(JWT, USERNAME);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(jwtManager);
    }

    /**
     * dado: un request sin token en el header Authorization.
     * cuando: el filtro intercepta la solicitud.
     * entonces: no se realiza autenticación y se continúa con el filtro.
     */
    @Test
    public void doFilterInternalShouldNotAuthenticateWhenNoTokenPresent() throws Exception {
        // arrange
        when(request.getHeader(anyString())).thenReturn(null);// extraccion no exitosa devulve el token nulo
        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // assert
        verify(jwtManager, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * dado: un token inválido.
     * cuando: el filtro intenta validarlo.
     * entonces: no se realiza autenticación.
     */
    @Test
    public void doFilterInternalShouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        // arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT);// extraccion exitosa
        when(jwtManager.extractUsername(anyString())).thenReturn(USERNAME);// username extraccion exitosa
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);// devolicion exitosa del mock
        when(jwtManager.isTokenValid(anyString(), anyString())).thenReturn(false);// error en la validacion del token
        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * dado: un token con un username que no existe.
     * cuando: se lanza UsernameNotFoundException.
     * entonces: no se realiza autenticación.
     * 
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void doFilterInternalShouldNotAuthenticateWhenUserNotFound()
            throws UsernameNotFoundException, IOException, ServletException {
        // arrang
        when(request.getHeader(anyString())).thenReturn("Bearer " + JWT);// extraccion exitosa del token
        when(jwtManager.extractUsername(anyString())).thenReturn(USERNAME);// extraccion del username exitosa
        // simulamos que no se encontro el usuario act
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException(anyString()));
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
                });

        // asser
        verify(filterChain, times(0)).doFilter(request, response);
    }
}
