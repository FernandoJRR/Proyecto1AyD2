
package com.hospitalApi.shared.exceptions;

/**
 *
 * Exepcion que indica que un reqeust no es correcta para ser procesada dentro
 * de la api..
 * 
 * @author Luis Monterroso
 */
public class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }

}
