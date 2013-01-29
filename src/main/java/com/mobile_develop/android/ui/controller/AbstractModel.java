package com.mobile_develop.android.ui.controller;

import java.util.ArrayList;

public class AbstractModel implements Model {
	
	private ArrayList<ModelListener> listeners;
	
	public AbstractModel()
	{
		this.listeners = new ArrayList<ModelListener>(1);
	}

	@Override
	public void addModelListener(ModelListener listener) throws Exception {
        if( listener != null ) {
            this.listeners.add(listener);
            if( this.listeners.size() == 1 ) {
                startedListening();
            }
        }
	}

	@Override
	public void removeModelListener(ModelListener listener) throws Exception {
		this.listeners.remove(listener);
        if( this.listeners.size() == 0 ) {
            stoppedListening();
        }
	}

    protected void startedListening() throws Exception {

    }

    protected void stoppedListening() throws Exception {

    }
	
	protected void fireModelEvent(int eventType, Object parameter) {
		for( int i=listeners.size(); i>0; ) {
			i--;
			ModelListener listener = this.listeners.get(i);
			listener.modelEvent(this, eventType, parameter);
		}
	}

	protected void fireModelChangeEvent(int changeType, Object parameter)
	{
		for( int i=listeners.size(); i>0; ) 
		{
			i--;
			ModelListener listener = this.listeners.get(i);
			listener.modelChanged(this, changeType, parameter);
		}
	}
}
