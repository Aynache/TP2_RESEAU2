package org.example.common;

import java.io.Serializable;

public class AuthenticationException extends Exception implements Serializable {

    public AuthenticationException(String message) {
        super(message);
    }
}