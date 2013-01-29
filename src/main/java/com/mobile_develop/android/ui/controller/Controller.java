package com.mobile_develop.android.ui.controller;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ViewHolder;

public interface Controller {
	
	List<Command> getCommands();
	
	ViewHolder getViewHolder();
	
	ControllerState getState();
	
	void init(ViewGroup container, View reuseView, boolean attachToContainer);

	void start() throws Exception;

	void stop() throws Exception;
	
	void destroy();

    String getTitle();
	
	void addStateListener(ControllerStateListener stateListener);
	
	void removeStateListener(ControllerStateListener stateListener);
	
	void addCommandListener(ControllerCommandListener commandListener);
	
	void removeCommandListener(ControllerCommandListener commandListener);
}
