package com.mobile_develop.android.ui.controller.composite.stack;

import java.util.List;
import java.util.Stack;

import android.view.View;
import android.view.ViewGroup;

import com.mobile_develop.android.ui.Action;
import com.mobile_develop.android.ui.AnimationFactory;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.Command.CommandStyle;
import com.mobile_develop.android.ui.Command.CommandType;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerState;
import com.mobile_develop.android.ui.controller.DecoratorControllerFactory;
import com.mobile_develop.android.ui.controller.Model;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;
import com.mobile_develop.android.ui.controller.composite.CompositeControllerEntry;

public class StackController<ModelType extends Model> extends AbstractCompositeController<ModelType> {
	
	public static final CommandType COMMAND_TYPE_BACK = new CommandType(-1, "Back", StackController.class.getSimpleName());
	
	private int pushViewId;
	
	private Stack<CompositeControllerEntry> stackControllers;
	
	private AnimationFactory pushAddAnimationFactory;
	private AnimationFactory pushRemoveAnimationFactory;
	private AnimationFactory popAddAnimationFactory;
	private AnimationFactory popRemoveAnimationFactory;
	
	protected Command backCommand;

	
	public StackController(
			ViewHolderFactory viewHolderFactory, 
			ErrorHandler errorHandler, 
			ThreadHelper threadHelper, 
			ViewHolderFactory snapshotViewHolderFactory, 
			DecoratorControllerFactory decoratorControllerFactory,
			int pushViewId, 
			AnimationFactory pushAddAnimationFactory,
			AnimationFactory pushRemoveAnimationFactory, 
			AnimationFactory popAddAnimationFactory, 
			AnimationFactory popRemoveAnimationFactory
	) {
		super(viewHolderFactory, errorHandler, threadHelper, snapshotViewHolderFactory, decoratorControllerFactory);
		this.pushViewId = pushViewId;
		this.stackControllers = new Stack<CompositeControllerEntry>();
		
		this.pushAddAnimationFactory = pushAddAnimationFactory;
		this.pushRemoveAnimationFactory = pushRemoveAnimationFactory;
		this.popAddAnimationFactory = popAddAnimationFactory;
		this.popRemoveAnimationFactory = popRemoveAnimationFactory;
		
		this.backCommand = new Command(
				COMMAND_TYPE_BACK, 
				new Action() {
					
					@Override
					public void perform() {
						try {
							pop();
						} catch( Exception ex ) {
							handleError(ex);
						}
					}
				}, 
				CommandStyle.Back, 
				1,
				true
		);
	}



	@Override
	protected ViewGroup getContainer(Controller controller) {
		return getChildView(pushViewId, true);
	}
	
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		super.start();
	}

	@Override
	protected void load(ModelType model) throws Exception {
		// probably do nothing
	}

    @Override
    public void destroy() {
        super.destroy();
        // destroy any active stack controllers
        for( int i=this.stackControllers.size()-1; i>0; ) {
            i--;
            CompositeControllerEntry entry = this.stackControllers.get(i);
            if( entry.getDecorator().getState() != ControllerState.Uninitialised ) {
                entry.getDecorator().destroy();
            }
        }
    }

    @Override
	protected List<Command> getInternalCommands() {
		List<Command> internalCommands = super.getInternalCommands();
		// check for an existing back operation
        if( backCommand != null ) {
            boolean backExists = false;
            for( int i=internalCommands.size(); i>0; ) {
                i--;
                Command internalCommand = internalCommands.get(i);
                if( internalCommand.getStyle() == Command.CommandStyle.Back ) {
                    backExists = true;
                    break;
                }
            }
            if( !backExists && stackControllers.size() > 1 ) {
                internalCommands.add(this.backCommand);
            }
        }
		return internalCommands;
	}

    public void deStack(Controller controller) throws Exception {
        // we remove this controller from anywhere in the stack
        if( this.stackControllers.peek().getController() == controller ) {
            pop();
        } else {
            for( int i=this.stackControllers.size(); i>0; ) {
                i--;
                CompositeControllerEntry entry = stackControllers.get(i);
                if( entry.getController() == controller ) {
                    this.stackControllers.remove(i);
                    break;
                }
            }
        }
    }

    public boolean isOnStack(Controller controller ) {
        for(CompositeControllerEntry entry  : this.stackControllers ) {
            if( entry.getController() == controller ) {
                return true;
            }
        }
        return false;
    }

	public void push(Controller controller) throws Exception {
        push(controller, this.pushAddAnimationFactory, this.pushRemoveAnimationFactory);
    }

    public void push(Controller controller, AnimationFactory pushAddAnimationFactory, AnimationFactory pushRemoveAnimationFactory) throws Exception {
		if( !stackControllers.empty() ) {
            CompositeControllerEntry top = stackControllers.peek();
			remove(top, pushRemoveAnimationFactory, false);
            if( getState() == ControllerState.Started ) {
                top.getDecorator().stop();
            }
            if( getState() == ControllerState.Initialised || getState() == ControllerState.Started ) {
                top.getDecorator().getViewHolder().detach();
            }
		}
		CompositeControllerEntry added = add(controller, pushAddAnimationFactory, true);
        this.stackControllers.push(added);
        if( this.stackControllers.size() == 2 ) {
            // we just added a back button
            rebuildCommands();
        }
	}
	
	public Controller pop() throws Exception {
        return pop(this.popAddAnimationFactory, this.popRemoveAnimationFactory);
    }

    public Controller pop(AnimationFactory popAddAnimationFactory, AnimationFactory popRemoveAnimationFactory)
        throws Exception {
		Controller result;
		if( !stackControllers.empty() ) {
			CompositeControllerEntry entry = stackControllers.pop();
			View animationView = remove(entry, popRemoveAnimationFactory, true);
            if( this.decoratorFactory != null ) {
                this.decoratorFactory.strip(entry.getDecorator());
            }

			if( !stackControllers.empty() ) {
                CompositeControllerEntry top = stackControllers.peek();
                boolean started = (top.getDecorator().getState() != ControllerState.Uninitialised);
				add(top, popAddAnimationFactory, !started);
                if( started ) {
                    top.getDecorator().getViewHolder().attach();
                    top.getDecorator().start();
                }
                // make sure that the copy of the view that is being removed stays on top of this view
                if( animationView != null ) {
                    animationView.bringToFront();
                }
			}
            result = entry.getController();
		} else {
			result = null;
		}
		return result;
	}
	
	public void popAll() throws Exception {
		while( pop() != null );
	}
}
