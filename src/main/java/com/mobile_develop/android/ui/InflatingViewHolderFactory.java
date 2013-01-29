package com.mobile_develop.android.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InflatingViewHolderFactory implements ViewHolderFactory {

    private Context context;
	private LayoutInflater inflater;
	private int layoutId;
	private LayoutParams params;

    public InflatingViewHolderFactory(Activity activity, int layoutId) {
        this(activity, activity.getLayoutInflater(), layoutId);
    }

	public InflatingViewHolderFactory(Context context, LayoutInflater inflater, int layoutId)
	{
		this(context, inflater, layoutId, null);
	}
	
	public InflatingViewHolderFactory(Context context, LayoutInflater inflater, int layoutId, LayoutParams params)
	{
		this.inflater = inflater;
		this.layoutId = layoutId;
		this.params = params;
	}
	
	public InflatingViewHolderFactory(Context context, LayoutInflater inflater, int layoutId, boolean fill)
	{
		this(
                context,
				inflater, 
				layoutId, 
				fill?
						(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)):
						(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
		);
	}
	
	@Override
	public ViewHolder createViewHolder(ViewGroup parent, View reuseView, boolean attachToParent) {
		List<View> views;
		if(reuseView == null ) 
		{
            if( attachToParent ) {
                // TODO this should be done more elegantly
                int beforeChildCount = parent.getChildCount();
                HashSet<View> beforeChildren = new HashSet<View>(beforeChildCount);
                for( int i=0; i<beforeChildCount; i++ ) {
                    View child = parent.getChildAt(i);
                    beforeChildren.add(child);
                }
                inflater.inflate(this.layoutId, parent, attachToParent);
                int afterChildCount = parent.getChildCount();
                views = new ArrayList<View>(Math.max(0, afterChildCount - beforeChildCount));
                for( int i=0; i<afterChildCount; i++ ) {
                    View child = parent.getChildAt(i);
                    if( !beforeChildren.contains(child) ) {
                        views.add(child);
                    }
                }
            } else {
                views = Arrays.asList(inflater.inflate(this.layoutId, parent, attachToParent));
            }
		} else {
			views = Arrays.asList(reuseView);
            if( parent != null && attachToParent ) {
                if( this.params == null ) {
                    parent.addView(reuseView);
                } else {
                    parent.addView(reuseView, this.params);
                }
            }
		}
		ViewGroup effectiveParent;
		if( attachToParent ) {
			effectiveParent = parent;
		} else {
			effectiveParent = null;
		}
		return new OwnedViewHolder(effectiveParent, views, "layoutId=0x"+Integer.toHexString(this.layoutId));
	}

    /* don't expect this to work
    private class CapturingLayoutInflater extends LayoutInflater {

        private ArrayList<View> captured;
        private View parent;

        public CapturingLayoutInflater(View parent, LayoutInflater original, Context newContext) {
            super(original, newContext);
            this.captured = new ArrayList<View>();
            this.parent = parent;
        }

        @Override
        public LayoutInflater cloneInContext(Context context) {
            return new CapturingLayoutInflater(parent, inflater, context);
        }

        @Override
        protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
            View created = super.onCreateView(parent, name, attrs);
            if( parent == this.parent ) {
                captured.add(created);
            }
            return created;
        }

        private List<View> expungeCaptured() {
            List<View> result = this.captured;
            this.captured = new ArrayList<View>();
            return result;
        }
    }
    */

}
