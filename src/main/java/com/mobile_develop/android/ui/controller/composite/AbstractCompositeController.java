package com.mobile_develop.android.ui.controller.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.mobile_develop.android.ui.Action;
import com.mobile_develop.android.ui.AnimationFactory;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolder;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.AbstractController;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerCommandListener;
import com.mobile_develop.android.ui.controller.ControllerState;
import com.mobile_develop.android.ui.controller.DecoratorControllerFactory;
import com.mobile_develop.android.ui.controller.Model;

public abstract class AbstractCompositeController<ModelType extends Model> extends AbstractController<ModelType> {
	
	@SuppressWarnings("unused")
	private static final String LOG_TAG = AbstractCompositeController.class.getSimpleName();
	
	protected ArrayList<CompositeControllerEntry> controllers;
	private ViewHolderFactory snapshotViewHolderFactory;
	protected DecoratorControllerFactory decoratorFactory;
	private ControllerCommandListener commandListener;
	
	public AbstractCompositeController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, ViewHolderFactory snapshotViewHolderFactory, DecoratorControllerFactory decoratorFactory)
	{
		this(viewHolderFactory, errorHandler, threadHelper, 1);
		this.snapshotViewHolderFactory = snapshotViewHolderFactory;
		this.decoratorFactory = decoratorFactory;
	}
	
	public AbstractCompositeController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, int size)
	{
		super(viewHolderFactory, errorHandler, threadHelper);
		this.controllers = new ArrayList<CompositeControllerEntry>(size);
		this.commandListener = new ControllerCommandListener() {
			
			@Override
			public void commandsChanged(Controller controller, List<Command> commands) {
				rebuildCommands();
			}
		};
	}

    @Override
    protected <ViewType extends View> ViewType getChildView(int viewId, boolean mandatory) {
        // be careful not to return child views
        List<View> views = getViews();
        View found = null;
        for( View view : views ) {
            found = getChildView(viewId, view);
            if( found != null ) {
                break;
            }
        }
        if( mandatory && found == null ) {
            StringBuffer viewIds = new StringBuffer();
            for( View view : views ) {
                viewIds.append(" 0x");
                viewIds.append(Integer.toString(view.getId(), 16));
            }

            throw new IllegalArgumentException("no view with id 0x"+Integer.toString(viewId, 16)+" in "+viewIds);
        }
        return (ViewType)found;
    }

    private View getChildView(int viewId, View view) {
        View result;

        if( view == null ) {
            result = null;
        } else if( view.getId() == viewId ) {
            result = view;
        } else {
            result = null;
            // obtain the child view
            if( view instanceof ViewGroup ) {
                ViewGroup viewGroup = (ViewGroup)view;
                int childCount = viewGroup.getChildCount();
                for( int i=childCount; i>0; ) {
                    i--;
                    View child = viewGroup.getChildAt(i);
                    // check that it's not part of a sub-controller
                    boolean ok = true;

                    outer: for( int j=this.controllers.size(); j>0; ) {
                        j--;
                        ViewHolder viewHolder = this.controllers.get(j).getDecorator().getViewHolder();
                        if( viewHolder != null ) {
                            List<View> childViews = viewHolder.getOwnedViews();
                            for( View childView : childViews ) {
                                if( childView == child ) {
                                    ok = false;
                                    break outer;
                                }
                            }
                        }
                    }

                    if( ok ) {
                        result = getChildView(viewId, child);
                        if( result != null ) {
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    public DecoratorControllerFactory getDecoratorFactory() {
        return decoratorFactory;
    }

    public void setDecoratorFactory(DecoratorControllerFactory decoratorFactory) {
        this.decoratorFactory = decoratorFactory;
    }

    @Override
	public void init(ViewGroup container, View reuseView, boolean attachToContainer) {
		super.init(container, reuseView, attachToContainer);
		for(int i=controllers.size(); i>0; ) 
		{
			i--;
			CompositeControllerEntry entry = controllers.get(i); 
			Controller childController = entry.getDecorator();
			ViewGroup childContainer = getContainer(entry.getController());
			if( childContainer != null ) {
				//throw new IllegalStateException("the child "+childController+" does not have a container view! Check the getContainer method of "+getClass().getName());
                childController.init(childContainer, null, true);
            }
		}
	}

	@Override
	public void start() throws Exception {
		for(int i=controllers.size(); i>0; ) 
		{
			i--;
			Controller childController = controllers.get(i).getDecorator();
            if( childController.getState() == ControllerState.Initialised ) {
                childController.start();
            }
		}
		super.start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		for(int i=controllers.size(); i>0; ) 
		{
			i--;
			Controller childController = controllers.get(i).getDecorator();
            if( childController.getState() == ControllerState.Started ) {
                childController.stop();
            }
		}
		
	}

	@Override
	public void destroy() {
		super.destroy();
		for(int i=controllers.size(); i>0; ) 
		{
			i--;
			Controller childController = controllers.get(i).getDecorator();
            if( childController.getState() == ControllerState.Initialised ) {
                childController.destroy();
            }
		}
	}

    @Override
    public String getTitle() {
        // find the first sub-controller that has a title
        String title = null;
        for( int i=0; i<controllers.size(); i++ ) {
            title = controllers.get(i).getDecorator().getTitle();
            if( title != null ) {
                break;
            }
        }
        return title;
    }

    public boolean contains(Controller controller ) {
		boolean contains = false;
		for( int i=this.controllers.size(); i>0; ) {
			i--;
			if( controllers.get(i).getController() == controller ) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public void add(Controller controller) throws Exception {
		add(controller, null, true);
	}
	
	public CompositeControllerEntry add(Controller controller, AnimationFactory animationFactory, boolean initStart) throws Exception {
		checkUIThread();
		Controller existing = null;
		for( int i=this.controllers.size(); i>0; ) 
		{
			i--;
			CompositeControllerEntry entry = this.controllers.get(i); 
			if( entry.getController() == controller ) 
			{
				this.controllers.remove(i);
				existing = entry.getDecorator();
				break;
			}
		}
		if( existing != null ) {
			throw new RuntimeException("controller already exists!");
		}
		if( controller.getState() != ControllerState.Uninitialised ) {
			throw new IllegalStateException("the controller being added is in the state "+controller.getState()+" expected it to be "+ControllerState.Uninitialised);
		}
		
		Controller decorator;
		if( this.decoratorFactory != null )
		{
			decorator = this.decoratorFactory.decorate(controller);
		}
		else
		{
			decorator = controller;
		}
        CompositeControllerEntry entry = new CompositeControllerEntry(controller, decorator);
        add(entry, animationFactory, initStart);
        return entry;
    }

    public void add(CompositeControllerEntry entry, AnimationFactory animationFactory, boolean initStart) throws Exception {
        Controller controller = entry.getController();
        Controller decorator = entry.getDecorator();
		if(this.controllers.add(entry))
		{
			ControllerState state = this.getState();
			if( state == ControllerState.Initialised || state == ControllerState.Started )
			{
                ViewGroup container = null;
                if( initStart ) {
                    container = getContainer(entry.getController());
                    decorator.init(container, null, true);
                    if( state == ControllerState.Started )
                    {
                        decorator.start();
                    }
                }
                if( animationFactory != null )
                {
                    if( container == null ) {
                        container = getContainer(entry.getController());
                    }
                    List<View> views = decorator.getViewHolder().getViews();
                    for( View view : views ) {
                        Animation animation = animationFactory.createAnimation(view, container);
                        // TODO remove this
                        view.setMinimumWidth(container.getMeasuredWidth());
                        view.setMinimumHeight(container.getMeasuredHeight());
                        // end remove
                        view.setAnimation(animation);
                        addAnimation(animation);
                    }
                }
			}
		}
		controller.addCommandListener(this.commandListener);
		rebuildCommands();
	}
	
	public void remove(Controller controller) throws Exception {
		remove(controller, null, true);
	}
	
	public void remove(Controller controller, final AnimationFactory animationFactory, boolean stopDestroy) throws Exception {
		checkUIThread();
        CompositeControllerEntry found = null;
		for( int i=this.controllers.size(); i>0; )
		{
			i--;
			CompositeControllerEntry entry = this.controllers.get(i); 
			if( entry.getController() == controller ) 
			{
				found = entry;
				break;
			}
		}
		if(found != null)
		{
            remove(found, animationFactory, stopDestroy);
            if( this.decoratorFactory != null ) {
                this.decoratorFactory.strip(found.getDecorator());
            }
		}
	}

    public View remove(CompositeControllerEntry entry, AnimationFactory animationFactory, boolean stopDestroy) throws Exception {

        View result = null;
        final Controller decorator = entry.getDecorator();
        Controller controller = entry.getController();
        final ControllerState state = this.getState();
        if( state == ControllerState.Initialised || state == ControllerState.Started )
        {
            // grab a snapshot of the controller
            if( animationFactory != null )
            {

                final ViewGroup container = getContainer(controller);
                // add, animate, remove
                List<View> views = decorator.getViewHolder().getViews();
                for( int i=0; i<views.size(); i++ ) {
                    View view = views.get(i);
                    boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
                    if( !drawingCacheEnabled )
                    {
                        view.setDrawingCacheEnabled(true);
                    }
                    view.buildDrawingCache();
                    Bitmap cache = view.getDrawingCache();
                    int measuredWidth = view.getMeasuredWidth();
                    int measuredHeight = view.getMeasuredHeight();
                    if( cache != null ) {
                        Bitmap bitmap = Bitmap.createBitmap(cache);
                        if( !drawingCacheEnabled )
                        {
                            view.setDrawingCacheEnabled(false);
                        }
                        final ViewHolder viewHolder = snapshotViewHolderFactory.createViewHolder(container, null, true);
                        final ImageView imageView = (ImageView)viewHolder.getViews().get(0);
                        result = imageView;
                        imageView.setImageBitmap(bitmap);
                        imageView.setMinimumWidth(measuredWidth);
                        imageView.setMinimumHeight(measuredHeight);
                        imageView.layout(0, 0, measuredWidth, measuredHeight);
                        Animation animation = animationFactory.createAnimation(imageView, container);
                        imageView.setAnimation(animation);
                        addAnimation(animation,
                            new Action(){
                                @Override
                                public void perform() {
                                    // suppress any further rendering to prevent weird flicker bug
                                    imageView.setImageBitmap(null);
                                    imageView.setAlpha(0);
                                    imageView.setWillNotDraw(true);
                                    // need to do this after the current event exits, otherwise we get weird errors
                                    Runnable r = new Runnable() {

                                        @Override
                                        public void run() {
                                            viewHolder.release();
                                        }
                                    };
                                    invokeLater(r);
                                }
                            }
                        );
                    }
                }
            }
            if( stopDestroy ) {
                if( state == ControllerState.Started )
                {
                    decorator.stop();
                }
                decorator.destroy();
            }
        }
        this.controllers.remove(entry);
        controller.removeCommandListener(this.commandListener);
        rebuildCommands();
        return result;
    }
	
	public void removeAll() throws Exception
	{
		for(int i=this.controllers.size(); i>0; ) {
			i--;
			Controller controller = this.controllers.get(i).getController();
			remove(controller);
		}
	}
	
	@Override
	protected List<Command> getInternalCommands() {
		ArrayList<Command> internalCommands = new ArrayList<Command>();
		for(int i=this.controllers.size(); i>0; ) {
			i--;
			Controller controller = this.controllers.get(i).getController();
			List<Command> childCommands = controller.getCommands();
			if( childCommands != null ) {
				internalCommands.addAll(childCommands);
			}
		}
		Collections.sort(internalCommands);
		return internalCommands;
	}

	protected abstract ViewGroup getContainer(Controller controller); 
}
