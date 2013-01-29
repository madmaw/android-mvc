package com.mobile_develop.android.ui.controller.error;

import android.view.View;
import android.widget.Button;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.composite.list.ControllerListContext;
import com.mobile_develop.android.ui.controller.composite.list.ListViewListController;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 21/12/11
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorListViewListController<ModelType extends ErrorListModel, ControllerIdType> extends ListViewListController<ModelType, ControllerIdType> {
    private int retryButtonId;

    public ErrorListViewListController(
            ViewHolderFactory viewHolderFactory,
            ErrorHandler errorHandler,
            ThreadHelper threadHelper,
            ControllerListContext<ModelType, ControllerIdType> context,
            int listViewId,
            int retryButtonId
    ) {
        super(viewHolderFactory, errorHandler, threadHelper, context, listViewId);
        this.retryButtonId = retryButtonId;
    }

    @Override
    public void start() throws Exception {
        super.start();
        Button button = getChildView(retryButtonId, true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getModel().requestRetry();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Button button = getChildView(retryButtonId, true);
        button.setOnClickListener(null);
    }
}
