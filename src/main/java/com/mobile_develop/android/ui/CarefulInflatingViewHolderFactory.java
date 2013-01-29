package com.mobile_develop.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ViewHolderFactory implementation that checks the id of the view being reused before it reuses it
 */
public class CarefulInflatingViewHolderFactory extends InflatingViewHolderFactory {
    private Integer viewId;

    public CarefulInflatingViewHolderFactory(Context context, LayoutInflater layoutInflater, int layoutId) {
        super(context, layoutInflater, layoutId);
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent, View reuseView, boolean attachToParent) {
        View actualReuseView;
        if( reuseView == null || viewId == null || reuseView.getId() != viewId.intValue() ) {
            actualReuseView = null;
        } else {
            actualReuseView = reuseView;
        }
        ViewHolder result = super.createViewHolder(parent, actualReuseView,  attachToParent);
        if( viewId == null ) {
            // should only have one view
            List<View> views = result.getViews();
            if( views.size() == 1 ) {
                viewId = views.get(0).getId();
            }
        }
        return result;
    }
}
