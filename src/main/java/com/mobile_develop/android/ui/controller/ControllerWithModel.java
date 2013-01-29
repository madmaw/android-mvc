package com.mobile_develop.android.ui.controller;

public interface ControllerWithModel<ModelType> extends Controller {

	ModelType getModel();
	
	void setModel(ModelType model) throws Exception;
}
