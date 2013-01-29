package com.mobile_develop.android.ui;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class OwnedViewHolder implements ViewHolder {

	private ViewGroup container;
	private List<View> views;
	private String name;
	
	public OwnedViewHolder(ViewGroup container, List<View> views, String name) {
		this.views = views;
		this.container = container;
		this.name = name;
	}

    @Override
    public List<View> getViews() {
        return this.views;
    }

    @Override
    public List<View> getOwnedViews() {
        return getViews();
    }

    @Override
	public View getChildView(int viewId) {
		View view = null;
        int i = 0;
        while( view == null && i < views.size() ) {
            view = views.get(i).findViewById(viewId);
            i++;
        }
        return view;
	}

	@Override
	public void release() {
        detach();
        this.container = null;
        this.views = null;
	}

    @Override
    public void attach() {
        if( this.container != null ) {
            for( int i=0; i<views.size(); i++ ) {
                View view = this.views.get(i);
                this.container.addView(view);
            }
        }

    }

    @Override
    public void detach() {
		// remove from parent
		if( this.container != null ) {
            for( int i=0; i<views.size(); i++ ) {
                View view = this.views.get(i);
                this.container.removeView(view);
            }
		}
    }

    @Override
	public String toString() {
		return this.name;
	}
}
