package com.hospitalApi.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hospitalApi.auth.jwt.filters.JwtAuthenticationFilter;
import com.hospitalApi.auth.login.ports.ForUserLoader;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // esto es necesario para activar el preauthorize (para proteger los
                                             // endpoinds en funcion de los permisos)
public class SecurityConfig {

    private final AppProperties appProperties;

    private final ForUserLoader forUserLoader;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Desactiva CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Activa CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() // Protege el resto de rutas
                )
                // sin sesiones
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // // Version con jwt activa y rutas con roles
        // http.csrf(csrf -> csrf.disable()) // Desactiva CSRF
        // .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Activa
        // CORS
        // .authorizeHttpRequests(auth -> auth
        // .requestMatchers("/api/v1/**").permitAll() // Permite rutas públicas

        // // configuración específica para rutas con /any/
        // .requestMatchers(new AntPathRequestMatcher("/api/**/any/**"))
        // .hasAnyRole("ADMINISTRATIVE", "CLINIC")
        // // configuracion de rutas administrativas
        // .requestMatchers(new
        // AntPathRequestMatcher("/api/**/admin/**")).hasAnyRole("ADMINISTRATIVE")
        // // rutas publicas
        // .requestMatchers(new AntPathRequestMatcher("/api/**/public/**")).permitAll()
        // .anyRequest().authenticated() // Protege el resto
        // )
        // .sessionManagement(session ->
        // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin
        // // sesiones
        // .addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);
        // // Agrega el filtro JWT

        return http.getOrBuild();
    }

    /**
     * Configuración de CORS personalizada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // agrega todas las rutas permitidas
        configuration.setAllowedOrigins(List.of(appProperties.getFrontendLocal(), appProperties.getFrontendDev()));

        // decimos que operaciones http estan permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // decimos que headers estan permitidos
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // permite cookies y credenciales
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // aplicamos CORS a todas las rutas del sistema
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura el bean que sera expueto cuando se necesite el cripter en el
     * sistema, se eligio esta implementacion porque utiliza BCrypt (version 2B para
     * compatibilidad con
     * caracteres especiales) y 12 iteraciones en el algoritmo.
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptVersion.$2B, 12);
    }

    /**
     * Configura el autenticationmanager, le da que implementacion del metodo
     * loadByUserName usara, asi como el econder
     * 
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(forUserLoader);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

}
