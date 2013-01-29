package com.mobile_develop.android.ui.controller.composite.factory;

import android.view.ViewGroup;

import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;

public class FactoryController extends AbstractCompositeController<FactoryModel> {
    private int containerId;

	public FactoryController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, int containerId) {
		super(viewHolderFactory, errorHandler, threadHelper, 1);
        this.containerId = containerId;
	}

	@Override
	protected ViewGroup getContainer(Controller controller) {
		return this.getChildView(containerId, true);
	}

	@Override
	protected void load(FactoryModel model) throws Exception {
		removeAll();
		add( model.createController() );
	}
}
