package com.mobile_develop.android.ui.controller.composite.async;

import com.mobile_develop.android.ui.controller.AbstractModel;
import com.mobile_develop.android.ui.controller.Model;

public class PassThroughAsynchronousStartModel<ModelType extends Model> extends AbstractModel implements
		AsynchronousStartModel<ModelType> {
	private ModelType model;
	
	public PassThroughAsynchronousStartModel(ModelType model) {
		this.model = model;
	}

	@Override
	public ModelType createModel() throws Exception {
		return model;
	}

}
