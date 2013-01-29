package com.mobile_develop.android.ui.controller;

public interface DecoratorControllerFactory {
	
	Controller decorate(Controller controller);
	
	void strip(Controller decorated);
	
}
