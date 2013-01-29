package com.mobile_develop.android.ui;

public interface ErrorHandler {
	
	void handleWarning(String errorType, String message, Throwable cause);
	
	void handleError(String errorType, String message, Throwable cause);
}
