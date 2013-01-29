package com.mobile_develop.android.ui;

import android.view.View;

import java.util.Arrays;
import java.util.List;

public class BorrowedViewHolder implements ViewHolder {

	private View view;
	
	public BorrowedViewHolder(View view)
	{
		this.view = view;
	}

    @Override
    public List<View> getViews() {
        return Arrays.asList(view);
    }

    @Override
    public List<View> getOwnedViews() {
        return Arrays.asList();
    }

    @Override
	public View getChildView(int viewId) {
		return this.view.findViewById(viewId);
	}

    @Override
    public void attach() {
        // do nothing
    }

    @Override
    public void detach() {
        // do nothing
    }

    @Override
	public void release() {
		// do nothing
	}
}
