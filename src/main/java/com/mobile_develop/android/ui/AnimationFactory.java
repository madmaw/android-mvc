package com.mobile_develop.android.ui;


import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

public interface AnimationFactory {
	
	Animation createAnimation(View view, ViewGroup container);
	
}
