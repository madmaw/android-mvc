package com.mobile_develop.android.ui;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 16/02/12
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOperation<ResultType> implements Operation<ResultType> {

    protected OperationObserver observer;
    protected boolean cancelable;
    protected boolean canceled;
    protected boolean reportsProgress;

    public AbstractOperation(boolean cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public ResultType perform(OperationObserver observer) throws Exception {
        this.observer = observer;
        this.canceled = false;
        return doPerform();
    }

    @Override
    public boolean cancel() {
        if( cancelable ) {
            this.canceled = true;
        }
        return cancelable;
    }

    protected abstract ResultType doPerform() throws Exception;

    @Override
    public boolean cancelable() {
        return this.cancelable;
    }

    public void setCancelable(boolean cancelable) {
        if( this.cancelable != cancelable ) {
            this.cancelable = cancelable;
            if( this.observer != null ) {
                this.observer.cancelableChanged(cancelable);
            }
        }
    }

    protected void fireOperationEvent(Integer value, Integer maxValue, String description) {
        if( this.observer != null ) {
            this.observer.progressChanged(value, maxValue, description);
        }
    }

    @Override
    public void performInMain(ResultType resultType) {
        // do nothing
    }

    public void setReportsProgress(boolean reportsProgress) {
        this.reportsProgress = reportsProgress;
    }

    @Override
    public boolean reportsProgress() {
        return reportsProgress;
    }
}
