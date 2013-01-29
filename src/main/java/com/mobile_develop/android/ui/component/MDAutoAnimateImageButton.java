package com.mobile_develop.android.ui.component;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class MDAutoAnimateImageButton extends ImageButton {
    private AnimationDrawable currentDrawableAnimation;
    private AnimationDrawable currentBackgroundAnimation;

    public MDAutoAnimateImageButton(Context context) {
        super(context);
        init();
    }

    public MDAutoAnimateImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MDAutoAnimateImageButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init();
    }

    private void init() {
        /*
        this.addOnAttachStateChangeListener(
                new OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View view) {
                    }

                    @Override
                    public void onViewDetachedFromWindow(View view) {
                        updateAnimationsState();
                    }
                }
        );
*/
    }

    private void updateAnimationsState() {
        boolean running = getVisibility() == View.VISIBLE && hasWindowFocus();
        if( running ) {
            AnimationDrawable drawable = getAnimationState(getDrawable());
            if( drawable != currentDrawableAnimation ) {
                if( currentDrawableAnimation != null ) {
                    currentDrawableAnimation.stop();
                }
                currentDrawableAnimation = drawable;
                if( currentDrawableAnimation != null ) {
                    currentDrawableAnimation.start();
                }
            }
            AnimationDrawable background = getAnimationState(getBackground());
            if( background != currentBackgroundAnimation ) {
                if( currentBackgroundAnimation != null ) {
                    currentBackgroundAnimation.stop();
                }
                currentBackgroundAnimation = background;
                if( currentBackgroundAnimation != null ) {
                    currentBackgroundAnimation.start();
                }
            }
        } else {
            if( currentDrawableAnimation != null ) {
                currentDrawableAnimation.stop();
                currentDrawableAnimation = null;
            }
            if( currentBackgroundAnimation != null ) {
                currentBackgroundAnimation.stop();
                currentBackgroundAnimation = null;
            }
        }
    }

    private AnimationDrawable getAnimationState(Drawable drawable) {
        while( drawable instanceof StateListDrawable ) {
            drawable = drawable.getCurrent();
        }
        if(drawable instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
            return animationDrawable;
        }
        return null;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateAnimationsState();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateAnimationsState();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updateAnimationsState();
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        updateAnimationsState();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateAnimationsState();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        updateAnimationsState();
    }

    // TODO handle more states
}
