package com.mobile_develop.android.ui.controller.composite.list;

import java.util.ArrayList;
import java.util.List;

import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerFactory;

public abstract class AbstractControllerListContext<ModelType> 
		implements ControllerListContext<ModelType, Integer> {
	
	private List<ControllerFactory> controllerFactories;
	protected ModelType model;
    private ArrayList<ControllerListContextListener> listeners;
    private ThreadHelper threadHelper;
	
	public AbstractControllerListContext(List<ControllerFactory> controllerFactories, ThreadHelper threadHelper)
	{
		this.controllerFactories = controllerFactories;
        this.listeners = new ArrayList<ControllerListContextListener>(1);
        this.threadHelper = threadHelper;
	}

    @Override
    public int getNumberOfControllerTypes() {
        return this.controllerFactories.size();
    }

    @Override
	public Controller newController(Integer controllerTypeId) throws Exception {
		return this.controllerFactories.get(controllerTypeId).createController();
	}

	@Override
	public void setModel(ModelType model) {
		this.model = model;
	}

    @Override
    public void addListener(ControllerListContextListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ControllerListContextListener listener) {
        this.listeners.remove(listener);
    }

    protected void fireListInvalidated() {
        threadHelper.invoke(new Runnable() {
            @Override
            public void run() {
                for( int i=listeners.size(); i>0; ) {
                    i--;
                    ControllerListContextListener listener = listeners.get(i);
                    listener.listInvalidated(AbstractControllerListContext.this);
                }
            }
        });
    }
}
