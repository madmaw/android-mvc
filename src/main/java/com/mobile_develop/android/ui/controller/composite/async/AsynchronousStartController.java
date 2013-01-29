package com.mobile_develop.android.ui.controller.composite.async;

import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerCommandListener;
import com.mobile_develop.android.ui.controller.ControllerState;
import com.mobile_develop.android.ui.controller.ControllerWithModel;
import com.mobile_develop.android.ui.controller.DecoratorControllerFactory;
import com.mobile_develop.android.ui.controller.Model;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;
import com.mobile_develop.android.ui.controller.error.ErrorListModel;
import com.mobile_develop.android.ui.controller.error.ErrorModel;
import com.mobile_develop.android.ui.controller.loading.LoadingModel;

public class AsynchronousStartController<ModelType extends Model>
    extends AbstractCompositeController<AsynchronousStartModel<ModelType>> {

	private static final String LOG_TAG = AsynchronousStartController.class.getSimpleName();

	private ControllerWithModel<ErrorListModel> errorListController;
	private ControllerWithModel<LoadingModel> loadingController;
	private ControllerWithModel<ModelType> contentController;
	private Controller contentControllerDecorator;
	
	private ControllerCommandListener commandListener;
	
	private AsynchronousStartErrorListModel errorListModel;

	private int viewIdContentPanel;
    private boolean successfullyLoaded;
	
	private DecoratorControllerFactory decoratorFactory;

	public AsynchronousStartController(
			ViewHolderFactory viewHolderFactory,
            ErrorHandler errorHandler,
			ThreadHelper threadHelper, 
			int viewIdContentPanel,
			ControllerWithModel<ErrorListModel> errorListController, 
			ControllerWithModel<LoadingModel> loadingController, 
			DecoratorControllerFactory decoratorFactory
	)
	{
		super(viewHolderFactory, errorHandler, threadHelper, 1);
		this.viewIdContentPanel = viewIdContentPanel;
		this.errorListModel = new AsynchronousStartErrorListModel(this);
		this.errorListController = errorListController;
		this.loadingController = loadingController;
		this.commandListener = new ControllerCommandListener() {
			@Override
			public void commandsChanged(Controller controller, List<Command> commands) {
				rebuildCommands();
			}
		};
		this.decoratorFactory = decoratorFactory;
	}
	
	public void setContentController(ControllerWithModel<ModelType> contentController) {
		if( this.contentController != null ) {
			this.contentController.removeCommandListener(commandListener);
		}
		this.contentController = contentController;
		if( this.contentController != null ) {
			this.contentController.addCommandListener(commandListener);
		}
        rebuildCommands();
	}

	public void handleWarning(String errorType, String message, Throwable cause) {
		this.errorListModel.addError(ErrorModel.ErrorLevel.Warning, errorType, message, cause);
	}
	
	@Override
	public void handleError(String errorType, String message, Throwable cause) {
		this.errorListModel.addError(ErrorModel.ErrorLevel.Error, errorType, message, cause);
		try {
			this.errorListController.setModel(this.errorListModel);
			Log.e(LOG_TAG, "logged error "+message, cause);
		} catch( Exception ex ) {
			Log.e(LOG_TAG, "error displaying error", ex);
			Log.e(LOG_TAG, "original error: "+message, cause);
		}
		if( !contains(this.errorListController ) ) {
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					try {
						removeAll();
                        // attempt to remove the content controller
                        try {
                            if( contentController.getState() == ControllerState.Started ) {
                                contentController.stop();
                            }
                        } catch( Exception ex ) {
                            errorListModel.addError(ErrorModel.ErrorLevel.Warning, DEFAULT_INTERNAL_ERROR_TYPE, DEFAULT_INTERNAL_ERROR_MESSAGE, ex);
                        }
                        try {
                            if( contentController.getState() == ControllerState.Initialised || contentController.getState() == ControllerState.Started ) {
                                contentController.destroy();
                            }
                        } catch( Exception ex ) {
                            errorListModel.addError(ErrorModel.ErrorLevel.Warning, DEFAULT_INTERNAL_ERROR_TYPE, DEFAULT_INTERNAL_ERROR_MESSAGE, ex);
                        }
						add(errorListController);
					} catch (Exception ex ) {
						Log.e(LOG_TAG, "unable to display error", ex );
					}
				}
			};
			invoke(r);
		}
	}


	@Override
	protected ViewGroup getContainer(Controller controller) {
		return this.getChildView(this.viewIdContentPanel, true);
	}

    public void displayMoreErrorDetail(String errorType, String message, Throwable cause) {
        this.errorHandler.handleError(errorType, message, cause);
    }

	@Override
	public void setModel(final AsynchronousStartModel<ModelType> model) {
		try {
			super.setModel(model);
			// set the model of the content controller
			// thread this if it is already started
			if( this.getState() == ControllerState.Started )
			{
				final AsynchronousStartLoadingModel loadingModel = new AsynchronousStartLoadingModel("Updating...", 2, 0);
				this.loadingController.setModel(loadingModel);
				this.add(this.loadingController);
				
				Thread thread = new Thread() {
	
					@Override
					public void run() {
						ModelType contentModel;
						try {
							contentModel = model.createModel();
							loadingModel.setCurrentStep(1);
							try {
								contentController.setModel(contentModel);
								loadingModel.setCurrentStep(2);
							} catch( Exception ex ) {
								handleError(
                                        ex
                                );
							}
							remove(loadingController);
						} catch( Exception ex ) {
							handleError(
                                    ex
                            );
							contentModel = null;
						}
					}
				};
				thread.start();
			}
		} catch( Exception ex ) {
			handleError(
                    ex
            );
		}
	}

	@Override
	protected void load(AsynchronousStartModel<ModelType> model) throws Exception {
		// do nothing
	}
	
	@Override
	public void init(ViewGroup container, View reuseView, boolean attachToContainer) {
		super.init(container, reuseView, attachToContainer);
		
	}

    @Override
    public String getTitle() {
        return this.contentController.getTitle();
    }

    private static int i = 0;

    @Override
	public void start() throws Exception {
        if( !successfullyLoaded ) {
            this.removeAll();
            // set the loading model
            final AsynchronousStartLoadingModel loadingModel = new AsynchronousStartLoadingModel("Starting...", 4, 0);
            this.loadingController.setModel(loadingModel);

            this.add(this.loadingController);
            // kick off the loading

            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        ModelType contentModel;
                        loadingModel.setCurrentStep(1);
                        // do not overwrite the model
                        if( contentController.getModel() == null ) {
                            contentModel = getModel().createModel();
                            loadingModel.setCurrentStep(2);
                            i++;
                            contentController.setModel(contentModel);
                        }
                        loadingModel.setCurrentStep(3);
                        invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if( contentControllerDecorator == null ) {
                                        if( decoratorFactory != null ) {
                                            contentControllerDecorator = decoratorFactory.decorate(contentController);
                                        } else {
                                            contentControllerDecorator = contentController;
                                        }
                                    }
                                    if( contentControllerDecorator.getState() == ControllerState.Uninitialised ) {
                                        ViewGroup contentContainer = getContainer(contentController);
                                        // add the content controller manually
                                        contentControllerDecorator.init(contentContainer, null, true);
                                    }
                                    if( contentControllerDecorator.getState() == ControllerState.Initialised ) {
                                        contentControllerDecorator.start();
                                    }
                                    fireCommandChangeEvent(getCommands());
                                    // unfortunately, it seems that we can't have focus if we don't have a focusable item inside us
                                    //if( getView().hasFocus() ) {
                                        contentControllerDecorator.getViewHolder().getViews().get(0).requestFocus();
                                    //}
                                    successfullyLoaded = true;
                                } catch( Exception ex ) {
                                    handleError(
                                            ex
                                    );
                                }
                            }
                        });
                        loadingModel.setCurrentStep(4);
                    } catch( Exception ex ) {
                        handleError(
                                ex
                        );
                    }

                    invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                remove(loadingController);
                            } catch( Exception ex ) {
                                handleError(
                                        ex
                                );
                            }
                        }
                    });
                }
            };
            threadHelper.threadExpensiveOperation(r);
        } else {
            if( this.contentControllerDecorator != null ) {
                this.contentControllerDecorator.start();
            }
        }
		super.start();		
	}	

	@Override
	public void stop() throws Exception {
		super.stop();
		// TODO this can only happen if the content controller has actually been started!
		if( this.contentControllerDecorator != null && this.contentControllerDecorator.getState() == ControllerState.Started ) {
			this.contentControllerDecorator.stop();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if( this.contentControllerDecorator != null && this.contentControllerDecorator.getState() == ControllerState.Initialised) {
			this.contentControllerDecorator.destroy();
			if( this.decoratorFactory != null ) {
				this.decoratorFactory.strip(this.contentControllerDecorator);
			}
			this.contentControllerDecorator = null;
		}
        successfullyLoaded = false;
	}

	@Override
	protected List<Command> getInternalCommands() {
		List<Command> internalCommands = super.getInternalCommands();
        if( this.contentController != null && this.contentController.getState() == ControllerState.Started ) {
            List<Command> contentCommands = this.contentController.getCommands();
            if( contentCommands != null ) {
                if( internalCommands == null ) {
                    internalCommands = contentCommands;
                } else {
                    // TODO merge
                    internalCommands.addAll(contentCommands);
                    Collections.sort(internalCommands);
                }
            }
        }
		return internalCommands;
	}

    void restart() {
        try {
            stop();
            this.successfullyLoaded = false;
            start();
        } catch( Exception ex ) {
            handleError(ex);
        }
    }
}
