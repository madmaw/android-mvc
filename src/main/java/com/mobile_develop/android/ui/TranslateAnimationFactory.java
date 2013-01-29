package com.mobile_develop.android.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class TranslateAnimationFactory implements AnimationFactory {

	private float xFrom;
	private float xTo;
	private float yFrom;
	private float yTo;
	private long duration;
	
	public TranslateAnimationFactory(long duration, float xFrom, float xTo, float yFrom, float yTo) {
		this.duration = duration;
		this.xFrom = xFrom;
		this.xTo = xTo;
		this.yFrom = yFrom;
		this.yTo = yTo;
	}
	
	@Override
	public Animation createAnimation(View view, ViewGroup container) {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 
				xFrom, 
				Animation.RELATIVE_TO_PARENT, 
				xTo, 
				Animation.RELATIVE_TO_PARENT, 
				yFrom, 
				Animation.RELATIVE_TO_PARENT, 
				yTo
		);
        //animation.setInterpolator(new LinearInterpolator());
		animation.setFillAfter(true);
		animation.setDuration(duration);
		return animation;
	}

}
