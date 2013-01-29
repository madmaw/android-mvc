package com.mobile_develop.android.ui.controller.composite.async;

import com.mobile_develop.android.ui.controller.AbstractModel;
import com.mobile_develop.android.ui.controller.loading.LoadingModel;

public class AsynchronousStartLoadingModel extends AbstractModel implements
		LoadingModel {

	private String message;
	private Integer maxStep;
	private Integer currentStep;
	
	public AsynchronousStartLoadingModel(String message, Integer maxStep, Integer currentStep) {
		this.message = message;
		this.maxStep = maxStep;
	}
	
	@Override
	public Integer getCurrentStep() {
		return this.currentStep;
	}

	@Override
	public Integer getMaxStep() {
		return this.maxStep;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public void setCurrentStep(Integer currentStep) {
		this.currentStep = currentStep;
		this.fireModelChangeEvent(CHANGE_TYPE_STEP, currentStep);
	}
}
