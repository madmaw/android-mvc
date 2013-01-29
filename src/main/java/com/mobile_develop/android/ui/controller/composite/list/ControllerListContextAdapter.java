package com.mobile_develop.android.ui.controller.composite.list;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.controller.AbstractController;
import com.mobile_develop.android.ui.controller.Controller;

public class ControllerListContextAdapter<ModelType, ControllerIdType> extends BaseAdapter {

	private ControllerListContext<ModelType, ControllerIdType> context;
	private ArrayList<Controller> activeControllers;
	private ErrorHandler errorHandler;
	
	public ControllerListContextAdapter(ControllerListContext<ModelType, ControllerIdType> context, ErrorHandler errorHandler)
	{
		this.context = context;
        context.addListener(new ControllerListContextListener() {
            @Override
            public void listInvalidated(ControllerListContext<?, ?> source) {
                ControllerListContextAdapter.this.notifyDataSetInvalidated();
            }
        });
		this.activeControllers = new ArrayList<Controller>();
		this.errorHandler = errorHandler;
	}

    @Override
    public int getViewTypeCount() {
        return context.getNumberOfControllerTypes();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO this is a shambles
        try {
            return (Integer)context.getControllerTypeId(position);
        } catch( Exception ex ) {
            throw new RuntimeException(ex);
        }
    }

    @Override
	public int getCount() {
        try {
            return this.context.getNumberOfRows();
        } catch( Exception ex ) {
            errorHandler.handleError("internal error", "unable to get count", ex);
            return 0;
        }
	}

	@Override
	public Object getItem(int rpw) {
		// this seems unnecessary
		return null;
	}

	@Override
	public long getItemId(int row) {
        try {
		    return this.context.getPersistentRowId(row);
        } catch( Exception ex ) {
            errorHandler.handleError("internal error", "unable to get persistent row id", ex);
            return 0;
        }
	}

	@Override
	public View getView(int row, View convertView, ViewGroup parent) {
		View view = null;
		try {
            ControllerIdType controllerTypeId = this.context.getControllerTypeId(row);
			Controller controller = this.context.newController(controllerTypeId);
			controller.init(parent, convertView, false);
            view = controller.getViewHolder().getViews().get(0);
			this.context.populateControllerModel(controller, row, controllerTypeId);
			controller.start();
			this.activeControllers.add(controller);
			// add to controllers
			// TODO this is a hack, the  list view seems to ignore any layout params
			view.setMinimumWidth( parent.getWidth() );
		} catch( Exception ex ) {
			errorHandler.handleError(
                    AbstractController.DEFAULT_INTERNAL_ERROR_TYPE,
                    AbstractController.DEFAULT_INTERNAL_ERROR_MESSAGE,
                    ex
            );
			// TODO use a placeholder view/error view
		}
		return view;
	}
	
	public void viewInactive(View view) {
		// shut down the associated controller
		for( int i=this.activeControllers.size(); i>0; ) 
		{
			i--;
			Controller activeController = this.activeControllers.get( i );
			if( activeController.getViewHolder().getViews().get(0) == view )
			{
				// TODO recycling this might be a nice thing to do
				try {
					activeController.stop();
				} catch( Exception ex ) {
					errorHandler.handleWarning(
                            AbstractController.DEFAULT_INTERNAL_ERROR_TYPE,
                            AbstractController.DEFAULT_INTERNAL_ERROR_MESSAGE,
                            ex
                    );
				}
				activeController.destroy();
				this.activeControllers.remove(i);
				break;
			}
		}
	}
}
