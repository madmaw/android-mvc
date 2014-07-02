package com.mobile_develop.android.ui;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 1/02/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OperationObserver<ProgressType> {
    void progressChanged(Integer value, Integer maxValue, ProgressType description);

    void cancelableChanged(boolean cancelable);
}
