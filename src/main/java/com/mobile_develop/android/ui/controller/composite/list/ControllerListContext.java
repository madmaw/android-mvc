package com.mobile_develop.android.ui.controller.composite.list;

import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.Model;

public interface ControllerListContext<ModelType, ControllerIdType> {
	
	Controller newController(ControllerIdType controllerTypeId) throws Exception;
	
	int getNumberOfRows() throws Exception;
	
	ControllerIdType getControllerTypeId(int row) throws Exception;
	
	long getPersistentRowId(int row) throws Exception;
	
	void populateControllerModel(Controller controller, int row, ControllerIdType controllerTypeId) throws Exception;
	
	void setModel(ModelType model) throws Exception;
	
	void listItemSelected(int row) throws Exception;

    void addListener(ControllerListContextListener listener);

    void removeListener(ControllerListContextListener listener);

    int getNumberOfControllerTypes();
}
