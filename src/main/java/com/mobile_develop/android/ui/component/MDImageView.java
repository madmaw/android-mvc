package com.mobile_develop.android.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 1/02/12
 * Time: 8:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MDImageView extends ImageView {

    private OnSizeChangedListener onSizeChangedListener;

    public MDImageView(Context context) {
        super(context);
    }

    public MDImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MDImageView(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
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
