package com.mobile_develop.android.ui;

import android.view.View;

import java.util.List;

public interface ViewHolder {
	
	List<View> getViews();

    List<View> getOwnedViews();

	View getChildView(int viewId);

	void release();

    void attach();

    void detach();
}
