package com.mobile_develop.android.ui;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 1/02/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Operation<ResultType, ProgressType> {
    ResultType perform(OperationObserver<ProgressType> observer) throws Exception;

    boolean cancelable();

    boolean cancel();

    void performInMain(ResultType resultType);

    boolean reportsProgress();
}
