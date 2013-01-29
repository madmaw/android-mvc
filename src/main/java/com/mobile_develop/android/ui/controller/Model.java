package com.mobile_develop.android.ui.controller;

public interface Model {
	
	void addModelListener(ModelListener listener) throws Exception;
	
	void removeModelListener(ModelListener listener) throws Exception;
}
