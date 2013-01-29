package com.mobile_develop.android.ui.controller;

public class AbstractModelProxy<ModelType extends Model> extends AbstractModel {

    protected ModelType proxied;
    private ModelListener modelListener;

    public AbstractModelProxy(ModelType proxied) {
        this.proxied = proxied;
        this.modelListener = new ModelListener() {
            @Override
            public void modelChanged(Model source, int changeType, Object parameter) {
                fireModelChangeEvent(changeType, parameter);
            }

            @Override
            public void modelEvent(Model source, int eventType, Object parameter) {
                fireModelEvent(eventType, parameter);
            }
        };
    }

    @Override
    protected void startedListening() throws Exception {
        proxied.addModelListener(this.modelListener);
    }

    @Override
    protected void stoppedListening() throws Exception {
        proxied.removeModelListener(this.modelListener);
    }
}
