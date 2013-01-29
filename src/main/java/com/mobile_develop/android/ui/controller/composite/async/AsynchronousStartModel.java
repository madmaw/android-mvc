package com.mobile_develop.android.ui.controller.composite.async;

import com.mobile_develop.android.ui.controller.Model;

public interface AsynchronousStartModel<ModelType extends Model> extends Model {
	
	public static final int NEW_MODEL_TYPE_AVAILABLE = 1;
	
	ModelType createModel() throws Exception;
}
