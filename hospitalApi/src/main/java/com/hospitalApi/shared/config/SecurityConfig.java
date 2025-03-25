package com.hospitalApi.shared.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import com.hospitalApi.permissions.enums.SystemPermissionEnum;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
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
                        // vamos a resguardar las rutas con los permisos necesarios

                        // Para PatientController
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/all")
                        .hasAuthority(SystemPermissionEnum.GET_ALL_PATIENTS.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/id/**")
                        .hasAuthority(SystemPermissionEnum.GET_PATIENT_BY_ID.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/dpi/**")
                        .hasAuthority(SystemPermissionEnum.GET_PATIENT_BY_DPI.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/create")
                        .hasAuthority(SystemPermissionEnum.CREATE_PATIENT.getPermission().getAction())

                        // para EmployeeController

                        .requestMatchers(HttpMethod.POST, "/api/v1/employees")
                        .hasAuthority(SystemPermissionEnum.CREATE_EMPLOYEE.getPermission().getAction())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/employees/*")
                        .hasAuthority(SystemPermissionEnum.EDIT_EMPLOYEE.getPermission().getAction())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/employees/*/desactivate")
                        .hasAuthority(SystemPermissionEnum.DESACTIVATE_EMPLOYEE.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/employees/*")
                        .hasAnyAuthority(SystemPermissionEnum.FIND_EMPLOYEE_BY_ID.getPermission().getAction(),
                                SystemPermissionEnum.EDIT_EMPLOYEE.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/employees/")
                        .hasAuthority(SystemPermissionEnum.FIND_ALL_EMPLOYEES.getPermission().getAction())

                        // para Medicine Controller
                        .requestMatchers(HttpMethod.GET, "/api/v1/medicines/all")
                        .hasAuthority(SystemPermissionEnum.GET_ALL_MEDICINES.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/medicines/low-stock")
                        .hasAuthority(SystemPermissionEnum.GET_LOW_STOCK_MEDICINES.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/medicines/*")
                        .hasAnyAuthority(SystemPermissionEnum.GET_MEDICINE_BY_ID.getPermission().getAction(),
                                SystemPermissionEnum.EDIT_MEDICINE.getPermission().getAction())

                        .requestMatchers(HttpMethod.POST, "/api/v1/medicines/create")
                        .hasAuthority(SystemPermissionEnum.CREATE_MEDICINE.getPermission().getAction())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/medicines/*")
                        .hasAuthority(SystemPermissionEnum.EDIT_MEDICINE.getPermission().getAction())

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/medicines/*")
                        .hasAuthority(SystemPermissionEnum.DELETE_MEDICINE.getPermission().getAction())

                        // PARA las salas

                        .requestMatchers(HttpMethod.POST, "/api/v1/sale-medicines/farmacia")
                        .hasAuthority(SystemPermissionEnum.CREATE_SALE_MEDICINE_FARMACIA.getPermission().getAction())

                        .requestMatchers(HttpMethod.POST, "/api/v1/sale-medicines/consult")
                        .hasAuthority(SystemPermissionEnum.CREATE_SALE_MEDICINE_CONSULT.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/sale-medicines/*")
                        .hasAuthority(SystemPermissionEnum.GET_SALE_MEDICINE_BY_ID.getPermission().getAction())

                        // para tipos de empleado:
                        .requestMatchers(HttpMethod.GET, "/api/v1/employee-types")
                        .hasAuthority(SystemPermissionEnum.GET_ALL_EMPLOYEE_TYPES.getPermission().getAction())

                        .requestMatchers(HttpMethod.GET, "/api/v1/employee-types/*")
                        .hasAnyAuthority(SystemPermissionEnum.GET_EMPLOYEE_TYPE_BY_ID.getPermission().getAction(),
                                SystemPermissionEnum.EDIT_EMPLOYEE_TYPE.getPermission().getAction())

                        .requestMatchers(HttpMethod.POST, "/api/v1/employee-types")
                        .hasAuthority(SystemPermissionEnum.CREATE_EMPLOYEE_TYPE.getPermission().getAction())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/employee-types/*")
                        .hasAuthority(SystemPermissionEnum.EDIT_EMPLOYEE_TYPE.getPermission().getAction())

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/employee-types/*")
                        .hasAuthority(SystemPermissionEnum.DELETE_EMPLOYEE_TYPE.getPermission().getAction())

                        // para los permisos

                        .requestMatchers(HttpMethod.GET, "/api/v1/permissions")
                        .hasAnyAuthority(SystemPermissionEnum.CREATE_EMPLOYEE_TYPE.getPermission().getAction(),
                                SystemPermissionEnum.EDIT_EMPLOYEE_TYPE.getPermission().getAction())

                        .anyRequest().authenticated() // Protege el resto de rutas
                )
                // sin sesiones
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.getOrBuild();
    }

    /**
     * Configuración de CORS personalizada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        System.out.println(appProperties.getFrontURL());
        CorsConfiguration configuration = new CorsConfiguration();
        // agrega todas las rutas permitidas
        configuration.setAllowedOrigins(List.of(appProperties.getFrontURL()));

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
