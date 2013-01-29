package com.mobile_develop.android.ui.controller;

public interface ModelListener {
	void modelChanged(Model source, int changeType, Object parameter);
	
	void modelEvent(Model source, int eventType, Object parameter);
}
