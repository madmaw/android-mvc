package com.mobile_develop.android.ui.controller.error;

import com.mobile_develop.android.ui.controller.Model;

public interface ErrorListModel extends Model {
	public static final int CHANGE_TYPE_ALL = 0;
	public static final int CHANGE_TYPE_ERROR_ADDED = 1;
	
	int getNumberOfErrors();
	
	ErrorModel getErrorModel(int index);

    void requestRetry();
}
