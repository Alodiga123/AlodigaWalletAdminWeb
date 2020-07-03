package com.alodiga.wallet.web.exceptions;

import org.apache.log4j.Logger;

public class MinAmountBalanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public MinAmountBalanceException(String message) {
        super(message);
    }

    public MinAmountBalanceException(Logger logger, String message, Exception e) {
        super(message, e);
        logger.error(message, e);
    }

    public MinAmountBalanceException(String message, StackTraceElement[] stackTrace) {
        super(message);
        setStackTrace(stackTrace);
    }
}
