package com.hospitalApi.shared.exceptions;

/**
 * Excepcion que indica que un login ha fallado.
 * 
 * @author Luis Monterroso
 */
public class BadCredentialsException extends Exception {

    public BadCredentialsException(String message) {
        super(message);
    }
}
