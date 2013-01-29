package com.mobile_develop.android.ui;

import android.view.View;
import android.view.ViewGroup;

public class SubordinateViewHolderFactory implements ViewHolderFactory {
	
	public SubordinateViewHolderFactory()
	{
		
	}

	@Override
	public ViewHolder createViewHolder(ViewGroup parent, View reuseView, boolean attachToContainer) {
		if( !attachToContainer ) {
			throw new RuntimeException("this view is always attached!");
		}
		return new BorrowedViewHolder(parent);
	}

}
