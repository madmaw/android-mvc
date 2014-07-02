package com.mobile_develop.android.ui.controller.loading;

import com.mobile_develop.android.ui.OperationObserver;
import com.mobile_develop.android.ui.controller.AbstractModel;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 16/02/12
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class OperationLoadingModel<ProgressType> extends AbstractModel implements LoadingModel {

    private Integer currentStep;
    private Integer maxStep;
    private String message;

    public OperationLoadingModel() {

    }

    public OperationObserver<ProgressType> createOperationObserver() {
        return new OperationObserver<ProgressType>() {
            @Override
            public void progressChanged(Integer value, Integer maxValue, ProgressType description) {
                currentStep = value;
                maxStep = maxValue;
                message = descriptionToString(description);
                fireModelChangeEvent(LoadingModel.CHANGE_TYPE_STEP, null);
            }

            @Override
            public void cancelableChanged(boolean cancelable) {

            }
        };
    }

    public String descriptionToString(ProgressType progressType) {
        String result;
        if( progressType != null ) {
            result = progressType.toString();
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public Integer getCurrentStep() {
        return currentStep;
    }

    @Override
    public Integer getMaxStep() {
        return maxStep;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
