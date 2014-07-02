package com.mobile_develop.android.ui;

public abstract class AbstractOperationProxy<ResultType, ProgressType, ProxiedResultType> implements Operation<ResultType, ProgressType> {

    private Operation<ProxiedResultType, ProgressType> proxied;

    public AbstractOperationProxy(Operation<ProxiedResultType, ProgressType> proxied) {
        this.proxied = proxied;
    }

    protected ResultType consume(ProxiedResultType from) throws Exception {
        return null;
    }

    @Override
    public ResultType perform(OperationObserver observer) throws Exception {
        ProxiedResultType proxiedResultType = proxied.perform(observer);
        return consume(proxiedResultType);
    }

    @Override
    public boolean cancelable() {
        return proxied.cancelable();
    }

    @Override
    public boolean cancel() {
        return proxied.cancel();
    }

    @Override
    public void performInMain(ResultType resultType) {
        this.proxied.performInMain(null);
    }

    @Override
    public boolean reportsProgress() {
        return proxied.reportsProgress();
    }
}
