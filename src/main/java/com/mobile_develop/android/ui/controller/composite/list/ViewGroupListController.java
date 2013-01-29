package com.mobile_develop.android.ui.controller.composite.list;

import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;

import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.Model;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;

import java.util.List;

public class ViewGroupListController<ModelType extends Model, ControllerIdType> extends AbstractCompositeController<ModelType> {
	
	private int viewGroupId;
	private ControllerListContext<ModelType, ControllerIdType> context;
	private OnClickListener onClickListener;
	
	public ViewGroupListController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, ControllerListContext<ModelType, ControllerIdType> context, int viewGroupId) {
		super(viewHolderFactory, errorHandler, threadHelper, 1);
		this.viewGroupId = viewGroupId;
		this.context = context;
        this.context.addListener(new ControllerListContextListener() {
            @Override
            public void listInvalidated(ControllerListContext<?, ?> source) {
                try {
                    ViewGroupListController.this.load(ViewGroupListController.this.getModel());
                } catch( Exception ex ) {
                    handleError(ex);
                }
            }
        });
		this.onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// find the source
				int row = 0;
				outer: while( row < controllers.size() ) {
                    List<View> views = controllers.get(row).getController().getViewHolder().getViews();
                    for( View view : views ) {
                        if( view == v ) {
                            try {
                                ViewGroupListController.this.context.listItemSelected(row);
                            } catch( Exception ex ) {
                                handleError(ex);
                            }
                            break outer;
                        }
                    }
					row++;
				}
			}
		};
	}

	@Override
	protected ViewGroup getContainer(Controller controller) {
		return getChildView(viewGroupId, true);
	}
	
	@Override
	public void setModel(ModelType model) throws Exception {
        //this.context.setModel(model);
        super.setModel(model);
	}

	@Override
	protected void load(ModelType model) throws Exception {
		this.removeAll();
        // TODO this is less-than elegant, should really listen for fine-grained changes on the context
        this.context.setModel(model);
		int rowCount = this.context.getNumberOfRows();
		for( int row = 0; row<rowCount; row++ ) {
			ControllerIdType controllerTypeId = this.context.getControllerTypeId(row);
			Controller controller = this.context.newController(controllerTypeId);
			this.context.populateControllerModel(controller, row, controllerTypeId);
			add(controller);
			//controller.getViewHolder().getView().setOnClickListener(onClickListener);
		}
	}
}
