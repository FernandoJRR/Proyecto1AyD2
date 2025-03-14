
package com.hospitalApi.shared.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la encriptación de contraseñas utilizando BCrypt.
 *
 * <p>
 * BCrypt genera un hash seguro para cada contraseña con un valor de "salting" y
 * un factor de costo configurable. Esto protege contra ataques de fuerza bruta
 * y rainbow tables.
 * </p>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Encriptar contraseñas con BCrypt.</li>
 * <li>Verificar contraseñas en formato hash seguro.</li>
 * </ul>
 *
 * 
 * @author Luis Monterroso
 * @see PasswordEncoderPort
 * @see BCryptPasswordEncoder
 */
@RequiredArgsConstructor
@Component
public class PasswordEncoderUtil {

    private final BCryptPasswordEncoder encoder;

    /**
     * Genera un hash seguro para la contraseña en texto plano proporcionada.
     *
     * @param rawPassword La contraseña en texto plano que se desea encriptar.
     * @return Un hash seguro generado usando BCrypt.
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword); // Genera el hash seguro
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su versión
     * encriptada.
     *
     * @param rawPassword     La contraseña en texto plano proporcionada por el
     *                        usuario.
     * @param encodedPassword El hash seguro almacenado en la base de datos.
     * @return {@code true} si la contraseña coincide, de lo contrario
     *         {@code false}.
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword); // Comparación segura
    }

}
