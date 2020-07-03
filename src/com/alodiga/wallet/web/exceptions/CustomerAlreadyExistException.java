package com.alodiga.wallet.web.exceptions;

public class CustomerAlreadyExistException extends Exception {

    private static final long serialVersionUID = 1L;

    public CustomerAlreadyExistException(String message) {
        super(message);
    }


}
