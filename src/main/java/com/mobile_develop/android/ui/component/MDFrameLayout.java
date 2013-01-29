package com.mobile_develop.android.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 1/02/12
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MDFrameLayout extends FrameLayout {

    private OnSizeChangedListener onSizeChangedListener;

    public MDFrameLayout(Context context) {
        super(context);
    }

    public MDFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MDFrameLayout(Context context, AttributeSet attrs, int style) {
        super( context, attrs, style );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if( this.onSizeChangedListener != null ) {
            this.onSizeChangedListener.sizeChanged(w, h, oldw, oldh);
        }
    }

    public OnSizeChangedListener getOnSizeChangedListener() {
        return onSizeChangedListener;
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }
}
