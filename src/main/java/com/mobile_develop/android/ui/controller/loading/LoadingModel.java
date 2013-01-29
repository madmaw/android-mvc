package com.mobile_develop.android.ui.controller.loading;

import com.mobile_develop.android.ui.controller.Model;

public interface LoadingModel extends Model {
	
	public static final int CHANGE_TYPE_STEP = 1;
	
	Integer getCurrentStep();
	
	Integer getMaxStep();
	
	String getMessage();
}
