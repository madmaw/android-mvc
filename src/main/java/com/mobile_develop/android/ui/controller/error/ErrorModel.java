package com.mobile_develop.android.ui.controller.error;

import com.mobile_develop.android.ui.controller.Model;

public interface ErrorModel extends Model {
	public enum ErrorLevel {
		Error, 
		Warning, 
		Info,
		Debug
	}

    String getErrorType();
	
	ErrorLevel getErrorLevel();
	
	String getMessage();
	
	String getDetail();

    boolean isDetailAvailable();

    void requestMoreDetail();
}
