package com.mobile_develop.android.ui.controller.command;

import android.view.ViewGroup;

import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerWithModel;
import com.mobile_develop.android.ui.controller.EmptyModel;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;

public class CommandBarDecoratorController extends
		AbstractCompositeController<EmptyModel> {
	
	private ControllerWithModel<CommandModel> commandController;
	//private Controller decoratedController;
	
	private int commandControllerViewId;
	private int decoratedControllerViewId;

	public CommandBarDecoratorController(
			ViewHolderFactory viewHolderFactory, 
			ErrorHandler errorHandler, 
			ThreadHelper threadHelper, 
			ControllerWithModel<CommandModel> commandController, 
			Controller decoratedController, 
			int commandControllerViewId, 
			int decoratedControllerViewId
	) throws Exception {
		super(viewHolderFactory, errorHandler, threadHelper, 2);
		this.commandController = commandController;
		//this.decoratedController = decoratedController;
		this.commandControllerViewId = commandControllerViewId;
		this.decoratedControllerViewId = decoratedControllerViewId;
		
		add(commandController);
		add(decoratedController);
	}
	
	public ControllerWithModel<CommandModel> getCommandController() {
		return this.commandController;
	}

	@Override
	protected ViewGroup getContainer(Controller controller) {
		ViewGroup result;
		if( controller == commandController ) {
			result = getChildView(commandControllerViewId, true);
		} else {
			result = getChildView(decoratedControllerViewId, true);
		}
		return result;
	}

	@Override
	protected void load(EmptyModel model) throws Exception {
		// do nothing
	}

    @Override
    public void start() throws Exception {
        super.start();
        // request focus
        // unfortunately, it seems that we can't have focus if we don't have a focusable item inside us
        //if( getView().hasFocus() ) {
            getViews().get(0).requestFocus();
        //}

    }
}
