package com.mobile_develop.android.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;

public class OrientationAwareViewHolderFactory implements ViewHolderFactory {

    private Activity activity;
    private ViewHolderFactory portraitViewHolderFactory;
    private ViewHolderFactory landscapeViewHolderFactory;

    public OrientationAwareViewHolderFactory(Activity activity, ViewHolderFactory portraitViewHolderFactory, ViewHolderFactory landscapeViewHolderFactory) {
        this.activity = activity;
        this.portraitViewHolderFactory = portraitViewHolderFactory;
        this.landscapeViewHolderFactory = landscapeViewHolderFactory;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent, View reuseView, boolean attachToParent) {
        ViewHolderFactory viewHolderFactory;
        if( activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            viewHolderFactory = this.landscapeViewHolderFactory;
        } else {
            viewHolderFactory = this.portraitViewHolderFactory;
        }
        return viewHolderFactory.createViewHolder(parent, reuseView, attachToParent);
    }
}
