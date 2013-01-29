package com.mobile_develop.android.ui.controller.composite;

import com.mobile_develop.android.ui.controller.Controller;

public class CompositeControllerEntry {
	private Controller controller;
	private Controller decorator;
	
	public CompositeControllerEntry(Controller controller, Controller decorator)
	{
		this.controller = controller;
		this.decorator = decorator;
	}
	
	public Controller getController()
	{
		return this.controller;
	}
	
	public Controller getDecorator()
	{
		return this.decorator;
	}
}
