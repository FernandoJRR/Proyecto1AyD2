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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hospitalApi.auth.jwt.utils.JwtManagerUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String JWT = "dsa.asd.asd";
    private static final String USERNAME = "user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT);
        // simular la extraccion del usuario
        when(jwtManager.extractUsername(JWT)).thenReturn(USERNAME);
        // cuando se haga el laod entonces devoleremos nuestro mock
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        // cuando se trate de verificar el token entonces devolemos true en senial de
        // que es valido
        when(jwtManager.isTokenValid(JWT, USERNAME)).thenReturn(true);
        // cuando a nuestro mock se le hagan los geters entonces devolvemos mas mocks
        when(userDetails.getUsername()).thenReturn(USERNAME);
        when(userDetails.getAuthorities()).thenReturn(null);

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
        when(request.getHeader("Authorization")).thenReturn(null);// extraccion no exitosa devulve el token nulo
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
        when(jwtManager.extractUsername(JWT)).thenReturn(USERNAME);// username extraccion exitosa
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);// devolicion exitosa del mock
        when(jwtManager.isTokenValid(JWT, USERNAME)).thenReturn(false);// error en la validacion del token
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
        when(request.getHeader("Authorization")).thenReturn("Bearer " + JWT);// extraccion exitosa del token
        when(jwtManager.extractUsername(JWT)).thenReturn(USERNAME);// extraccion del username exitosa
        when(userDetailsService.loadUserByUsername(USERNAME)).thenThrow(new UsernameNotFoundException(anyString()));// simulamos
                                                                                                                    // que
                                                                                                                    // no
                                                                                                                    // se
                                                                                                                    // encontro
                                                                                                                    // el
                                                                                                                    // usuario
        // act
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
                });

        // asser
        verify(filterChain, times(0)).doFilter(request, response);
    }
}
