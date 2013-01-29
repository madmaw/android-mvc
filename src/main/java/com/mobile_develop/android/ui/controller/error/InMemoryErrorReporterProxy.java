package com.mobile_develop.android.ui.controller.error;

import java.util.ArrayList;
import java.util.List;

public class InMemoryErrorReporterProxy implements ErrorReporter {

    public static class Error {
        private String message;
        private Throwable cause;

        public Error(String message, Throwable cause) {
            this.message = message;
            this.cause = cause;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getCause() {
            return cause;
        }

    }

    private List<Error> errors;
    private ErrorReporter proxied;

    public InMemoryErrorReporterProxy() {
        this.errors = new ArrayList<Error>();
    }

    @Override
    public void reportError(String message, Throwable cause) {
        if( this.proxied != null ) {
            this.proxied.reportError(message, cause);
        } else {
            this.errors.add(new Error(message, cause));
        }
    }

    public void setProxied( ErrorReporter proxied ) {
        this.proxied = proxied;
        if( proxied != null ) {
            for(InMemoryErrorReporterProxy.Error error : errors ) {
                proxied.reportError(error.getMessage(), error.getCause());
            }
            this.errors.clear();
        }
    }
}
