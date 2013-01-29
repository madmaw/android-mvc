package com.mobile_develop.android.ui;

import android.view.View;
import android.view.ViewGroup;

public interface ViewHolderFactory {
	
	ViewHolder createViewHolder(ViewGroup parent, View reuseView, boolean attachToParent);
	
}
