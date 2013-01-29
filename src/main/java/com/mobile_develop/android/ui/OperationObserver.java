package com.mobile_develop.android.ui;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 1/02/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OperationObserver {
    void progressChanged(Integer value, Integer maxValue, String description);

    void cancelableChanged(boolean cancelable);
}
