package com.mobile_develop.android.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mobile_develop.android.ui.Action;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolder;
import com.mobile_develop.android.ui.ViewHolderFactory;

public abstract class AbstractController<ModelType extends Model> implements ControllerWithModel<ModelType> {
	
	private static final String LOG_TAG = AbstractController.class.getSimpleName();

    public static String DEFAULT_INTERNAL_ERROR_TYPE = "Internal Error";
    public static String DEFAULT_INTERNAL_ERROR_MESSAGE = "Please report this problem";
	
	private ViewHolderFactory viewHolderFactory;
	private ViewHolder viewHolder;
	
	private ControllerState controllerState;	
	
	private ArrayList<ControllerStateListener> stateListeners;
	private ArrayList<ControllerCommandListener> commandListeners;
	
	private ArrayList<Animation> activeAnimations;

	private ModelType model;
	private ModelListener modelListener;
	
	protected ErrorHandler errorHandler;
	protected ThreadHelper threadHelper;
	
	private String name;
	
	private List<Command> externalCommands;
	private ArrayList<Command> commands;
	
	public AbstractController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper) {
		this.viewHolderFactory = viewHolderFactory;
		this.errorHandler = errorHandler;
		this.threadHelper = threadHelper;
		
		this.controllerState = ControllerState.Uninitialised;
		this.stateListeners = new ArrayList<ControllerStateListener>(1);
		this.commandListeners = new ArrayList<ControllerCommandListener>(1);
		this.activeAnimations = new ArrayList<Animation>(1);
		this.modelListener = new ModelListener() {
			@Override
			public void modelChanged(Model source, final int changeType, final Object parameter) {
                invoke(new Runnable() {
                    @Override
                    public void run() {
                        handleChangeEvent(changeType, parameter);
                    }
                });
			}

			@Override
			public void modelEvent(Model source, final int eventType, final Object parameter) {
                invoke(new Runnable() {
                    @Override
                    public void run() {
                        handleEvent(eventType, parameter);
                    }
                });
			}
		};
	}
	
	@Override
	public List<Command> getCommands() {
		return this.commands;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ModelType getModel() {
		return this.model;
	}
	
	@Override
	public void setModel(ModelType model) throws Exception {
		if( this.model != null && this.controllerState == ControllerState.Started ) {
			this.model.removeModelListener(this.modelListener);
		}
		this.model = model;
		
		if( this.model != null && this.controllerState == ControllerState.Started ) {
			this.model.addModelListener(this.modelListener);
			load(model);
		}
	}
	
	public void addAnimation(Animation animation, final Action...completionActions) {
		animation.setAnimationListener(
			new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation a) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation a) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation a) {
					activeAnimations.remove(a);
					for( int i=0; i<completionActions.length; i++ ) 
					{
						Action completionAction = completionActions[i];
						completionAction.perform();
					}									
				}
			}
				
		);
		this.activeAnimations.add(animation);
		if( this.getState() == ControllerState.Started ) {
			animation.startNow();
		}
	}
	
	public void removeAnimation(Animation animation) {
		if( this.activeAnimations.remove(animation) ) {
			//animation.cancel();

		}
	}

	@Override
	public ViewHolder getViewHolder() {
		return this.viewHolder;
	}

	@Override
	public ControllerState getState() {
		return this.controllerState;
	}
	
	public void setState(ControllerState controllerState)
	{
		if(this.controllerState != controllerState)
		{
			ControllerState oldControllerState = this.controllerState;
			this.controllerState = controllerState;
			fireStateChangeEvent(oldControllerState, controllerState);
		}
	}

    protected void handleChangeEvent(int changeType, Object parameter) {
        try
        {
            load(AbstractController.this.model);
        } catch( Exception ex ) {
            handleError(ex);
        }
    }
	
	protected void handleEvent(int eventType, Object parameter ) {
		// do nothing
		Log.w(LOG_TAG, "unhandled event "+eventType);
	}

	@Override
	public void init(final ViewGroup container, final View reuseView, boolean attachToContainer) {
		checkState(ControllerState.Uninitialised, null);
		this.viewHolder = this.viewHolderFactory.createViewHolder(container, reuseView, attachToContainer);	
		setState(ControllerState.Initialised);
	}

	@Override
	public void start() throws Exception{
		checkState(ControllerState.Initialised, ControllerState.Started);
		setState(ControllerState.Started);
		if( this.model != null ) {
			this.model.addModelListener(this.modelListener);
		}
		load(this.model);
		attachListeners();
		for(int i=this.activeAnimations.size(); i>0; ) {
			i--;
			this.activeAnimations.get(i).start();
		}
	}

    @Override
	public void stop() throws Exception {

		checkState(ControllerState.Started, ControllerState.Initialised);
		detachListeners();
		setState(ControllerState.Initialised);
		if( this.model != null ) {
			this.model.removeModelListener(this.modelListener);
		}
		for(int i=this.activeAnimations.size(); i>0; ) {
			i--;
			// TODO we want the animation to finish up (fillBefore?)
			//this.activeAnimations.get(i).cancel();
		}
        List<View> views = getViews();
        for( View view : views ) {
            view.clearAnimation();
        }
	}

	@Override
	public void destroy() {
		checkState(ControllerState.Initialised, null);
		this.viewHolder.release();
		this.viewHolder = null;
		setState(ControllerState.Uninitialised);
	}

	@Override
	public void addStateListener(ControllerStateListener stateListener) {
		this.stateListeners.add(stateListener);
	}

	@Override
	public void removeStateListener(ControllerStateListener stateListener) {
		this.stateListeners.remove(stateListener);
	}

	@Override
	public void addCommandListener(ControllerCommandListener commandListener) {
		this.commandListeners.add(commandListener);
	}

	@Override
	public void removeCommandListener(ControllerCommandListener commandListener) {
		this.commandListeners.remove(commandListener);
	}

	protected abstract void load(ModelType model) throws Exception;
	
	protected void attachListeners() {
		
	}
	
	protected void detachListeners() {
		
	}
	
	protected void fireStateChangeEvent(ControllerState from, ControllerState to)
	{
		for( int i=this.stateListeners.size(); i>0; ) 
		{
			i--;
			this.stateListeners.get(i).stateChanged(this, from, to);
		}
	}
	
	protected void fireCommandChangeEvent(List<Command> commands) {
		for( int i=this.commandListeners.size(); i>0; ) {
			i--;
			this.commandListeners.get(i).commandsChanged(this, commands);
		}
	}
	
	protected List<View> getViews()
	{
		List<View> views;
		if( this.viewHolder != null ) 
		{
			views = this.viewHolder.getViews();
		} else {
			throw new RuntimeException("controller must be initialised");
		}
		return views;
	}

    protected <ViewType extends View> ViewType getChildView(int viewId) {
        return getChildView(viewId, false);
    }


    @SuppressWarnings("unchecked")
	protected <ViewType extends View> ViewType getChildView(int viewId, boolean mandatory)
	{
		View view;
		if( this.viewHolder != null ) 
		{
			view = this.viewHolder.getChildView(viewId);
		} else {
			throw new RuntimeException("controller must be initialised");
		}
		if( view == null && mandatory ) {
			throw new IllegalArgumentException("missing mandatory view 0x"+Integer.toHexString(viewId)+" in view holder "+this.viewHolder);
		}
		return (ViewType)view;
	}
	
	protected void rebuildCommands() {
		int size = 0;
		List<Command> internalCommands = getInternalCommands();
		List<Command> externalCommands = getExternalCommands();
		if( internalCommands != null ) {
			size += internalCommands.size();
		}
		if( externalCommands != null ) {
			size += externalCommands.size();
		}
		if( this.commands != null ) {
			this.commands.clear();
			this.commands.ensureCapacity(size);
		} else {
			this.commands = new ArrayList<Command>(size);
		}
		if( internalCommands != null && externalCommands != null ) {
			// TODO merge instead of re-sort
			this.commands.addAll(internalCommands);
			this.commands.addAll(externalCommands);
			Collections.sort(this.commands);
		} else if( internalCommands != null ) {
			this.commands.addAll(internalCommands);
		} else if( externalCommands != null ) {
			this.commands.addAll(externalCommands);
		}
		// fire command changed event
        threadHelper.invoke(new Runnable() {
            @Override
            public void run() {
                fireCommandChangeEvent(commands);
            }
        });
	}
	
	protected List<Command> getInternalCommands() {
		return null;
	}
	
	public List<Command> getExternalCommands() {
		return this.externalCommands;
	}
	
	public void setExternalCommands(List<Command> externalCommands) {
		this.externalCommands = externalCommands;
        rebuildCommands();
	}

	protected void handleError(Throwable cause)
	{
		handleError(DEFAULT_INTERNAL_ERROR_TYPE, DEFAULT_INTERNAL_ERROR_MESSAGE, cause);
	}
	
	protected void handleError(String errorType, String message, Throwable cause)
	{
		if( errorHandler != null ) {		
			errorHandler.handleError(errorType, message, cause);
		} else {
			Log.e(LOG_TAG, message, cause);
		}
	}
	
	protected void invokeLater(Runnable r ) {
		this.threadHelper.invokeLater(makeSafe(r));
	}
	
	protected void invoke(Runnable r) {
		this.threadHelper.invoke(makeSafe(r));
	}

	protected void invokeAndWait(Runnable r) {
		this.threadHelper.invokeAndWait(makeSafe(r));
	}

    private Runnable makeSafe(final Runnable r) {
        return new Runnable() {
            @Override
            public void run() {
                if( getState() == ControllerState.Started ) {
                    try {
                       r.run();
                    } catch( Exception ex ) {
                        handleError("Internal Error", "An internal error occurred", ex);
                    }
                } else {
                    Log.w(getClass().getName(), "discarded safe runnable!");
                }
            }
        };
    }
	
	protected void checkState(ControllerState expectedState, ControllerState targetState) {
		if( controllerState != expectedState && controllerState != targetState ) {
			throw new IllegalStateException(controllerState.name() + " != "+ expectedState.name()+" (class: "+getClass().getSimpleName()+" name: "+name+")" );
		}
		checkUIThread();
	}
	
	protected void checkUIThread() {
		if( !this.threadHelper.isUIThread() ) {
			throw new IllegalThreadStateException("needs to be in UI thread");
		}		
	}
}
