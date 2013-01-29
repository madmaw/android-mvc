package com.mobile_develop.android.ui.controller.error;

import java.util.Arrays;

import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerFactory;
import com.mobile_develop.android.ui.controller.ControllerWithModel;
import com.mobile_develop.android.ui.controller.composite.list.AbstractControllerListContext;

public class ErrorControllerListContext 
		extends AbstractControllerListContext<ErrorListModel> {
	
	public ErrorControllerListContext(ControllerFactory errorControllerFactory, ThreadHelper threadHelper)
	{
		super(Arrays.asList(errorControllerFactory), threadHelper);
	}
	
	@Override
	public int getNumberOfRows() {
		return this.model.getNumberOfErrors();
	}

	@Override
	public Integer getControllerTypeId(int row) {
		return 0;
	}

	@Override
	public long getPersistentRowId(int row) {
		// we never remove errors, so rows are effectively persistent
		return row;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void populateControllerModel(Controller controller, int row, Integer controllerTypeId) throws Exception {
		ControllerWithModel<ErrorModel> errorController = (ControllerWithModel<ErrorModel>)controller;
		ErrorModel errorModel = this.model.getErrorModel(row);
		errorController.setModel(errorModel);
	}

	@Override
	public void listItemSelected(int row) {
		// do nothing (error detail?)
	}

	
}
