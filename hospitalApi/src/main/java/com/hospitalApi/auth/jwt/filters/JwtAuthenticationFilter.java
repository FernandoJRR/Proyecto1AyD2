package com.hospitalApi.auth.jwt.filters;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hospitalApi.auth.jwt.utils.JwtManagerUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtManagerUtil jwtManager;
    private final UserDetailsService userDetailsService;

    /**
     * Método principal que intercepta cada solicitud HTTP y ejecuta la
     * validación del token JWT.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> tokenOptional = extractTokenFromHeader(request);

        if (tokenOptional.isEmpty()) {
            return;
        }

        String token = tokenOptional.get();
        Optional<UserDetails> userDetailsOptional = validateToken(token);

        if (userDetailsOptional.isEmpty()) {
            return;
        }

        authenticateUser(userDetailsOptional.get(), token, request);
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del encabezado `Authorization` de la solicitud.
     *
     * @param request solicitud HTTP de la que se extraerá el token.
     * @return optional que contiene el token JWT si está presente y
     *         es válido.
     */
    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    /**
     * Valida el token JWT y devuelve los detalles del usuario si el token es
     * válido.
     *
     * @param jwt El token JWT a validar.
     * @return Optional con los detalles del usuario si el token es
     *         válido.
     */
    private Optional<UserDetails> validateToken(String jwt) {
        try {
            String username = jwtManager.extractUsername(jwt);

            // cargar al usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // si esta invalido el token entonces lanzar un optional vacio
            if (!jwtManager.isTokenValid(jwt, userDetails.getUsername())) {
                return Optional.empty();
            }

            return Optional.of(userDetails);
        } catch (UsernameNotFoundException ex) {
            return Optional.empty();
        }
    }

    /**
     * Carga al usuario y lo establece el contexto de seguridad de Spring.
     *
     * @param userDetails detalles del usuario autenticado.
     * @param token       el token JWT usado para la autenticación.
     * @param request     la solicitud HTTP actual.
     */
    private void authenticateUser(UserDetails userDetails, String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
