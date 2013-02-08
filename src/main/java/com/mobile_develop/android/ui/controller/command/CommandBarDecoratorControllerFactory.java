package com.mobile_develop.android.ui.controller.command;

import android.app.Activity;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.*;

public class CommandBarDecoratorControllerFactory implements
		DecoratorControllerFactory {
	private ViewHolderFactory decoratorViewHolderFactory;
	private ViewHolderFactory commandBarViewHolderFactory;
	private ErrorHandler errorHandler;
	private ThreadHelper threadHelper;
	private CommandViewFactory commandViewFactory;
	
	private int generalButtonContainerId;
	private int backButtonContainerId;
    private int moreButtonContainerId;
	private int commandControllerViewId;
	private int decoratedControllerViewId;
    private int titleTextViewId;
    private int noTitleViewId;
    private CommandPopupMenuHandler popupMenuHandler;

    private int maxBarButtons;

    private Activity activity;
	private Controller source;

    private CommandMenuManager commandMenuManager;

    private ControllerStateListener controllerStateListener;
    
	public CommandBarDecoratorControllerFactory(
            Activity activity,
			Controller source,
			ViewHolderFactory decoratorViewHolderFactory, 
			int commandControllerViewId, 
			int decoratedControllerViewId,
			ViewHolderFactory commandBarViewHolderFactory,
			int generalButtonContainerId, 
			int backButtonContainerId,
            int moreButtonContainerId,
            int titleTextViewId,
            int noTitleViewId,
            CommandPopupMenuHandler popupMenuHandler,
			ErrorHandler errorHandler,
			ThreadHelper threadHelper, 
			CommandViewFactory commandViewFactory,
            final CommandMenuManager commandMenuManager,
            int maxBarButtons
	) {
        this.activity = activity;
		this.source = source;
		this.decoratorViewHolderFactory = decoratorViewHolderFactory;
		this.commandBarViewHolderFactory = commandBarViewHolderFactory;
		this.errorHandler = errorHandler;
		this.threadHelper = threadHelper;
		this.commandViewFactory = commandViewFactory;
		this.generalButtonContainerId = generalButtonContainerId;
		this.backButtonContainerId = backButtonContainerId;
        this.moreButtonContainerId = moreButtonContainerId;
		this.commandControllerViewId = commandControllerViewId;
		this.decoratedControllerViewId = decoratedControllerViewId;
        this.titleTextViewId = titleTextViewId;
        this.noTitleViewId = noTitleViewId;
        this.commandMenuManager = commandMenuManager;
        this.maxBarButtons = maxBarButtons;
        this.popupMenuHandler = popupMenuHandler;
        this.controllerStateListener = new ControllerStateListener() {
            @Override
            public void stateChanged(Controller source, ControllerState from, ControllerState to) {
                if( commandMenuManager != null ) {
                    ControllerWithModel<CommandModel> controller = (ControllerWithModel<CommandModel>)source;
                    CommandModel commandModel = controller.getModel();
                    if( to == ControllerState.Started ) {
                        commandMenuManager.appendCommandModel(commandModel);
                    } else if( to == ControllerState.Initialised && from == ControllerState.Started ) {
                        commandMenuManager.removeCommandModel(commandModel);
                    }
                }
            }
        };

	}
	
	public void setSource(Controller source) {
		this.source = source;
	}
	
	@Override
	public Controller decorate(Controller controller) {
		
		CommandController commandController = new CommandController(
				commandBarViewHolderFactory, 
				errorHandler, 
				threadHelper, 
				commandViewFactory, 
				generalButtonContainerId, 
				backButtonContainerId,
                moreButtonContainerId,
                titleTextViewId,
                noTitleViewId,
                popupMenuHandler
		);
		Controller decoratorController;
		try {
			final ControllerCommandModel commandModel = new ControllerCommandModel(source, activity, maxBarButtons);
			commandController.setModel(commandModel);
            commandController.addStateListener(this.controllerStateListener);
			decoratorController = new CommandBarDecoratorController(
					decoratorViewHolderFactory, 
					errorHandler, 
					threadHelper, 
					commandController, 
					controller, 
					commandControllerViewId, 
					decoratedControllerViewId
			);
		} catch( Exception ex ) {
			errorHandler.handleError(
                    AbstractController.DEFAULT_INTERNAL_ERROR_TYPE,
                    AbstractController.DEFAULT_INTERNAL_ERROR_MESSAGE,
                    ex
            );
			decoratorController = controller;
		}
		return decoratorController;
	}
	
	
	
	@Override
	public void strip(Controller decorated) {
		CommandBarDecoratorController controller = (CommandBarDecoratorController)decorated;
		ControllerCommandModel commandModel = (ControllerCommandModel)controller.getCommandController().getModel();
        controller.removeStateListener(this.controllerStateListener);
        if( commandMenuManager != null ) {
            commandMenuManager.removeCommandModel(commandModel);
        }
		this.source.removeCommandListener(commandModel);
	}

}
