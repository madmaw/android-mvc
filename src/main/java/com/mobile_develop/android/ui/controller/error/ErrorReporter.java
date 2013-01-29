package com.mobile_develop.android.ui.controller.error;

public interface ErrorReporter {
    void reportError(String message, Throwable cause);
}
