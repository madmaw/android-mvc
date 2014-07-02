package com.mobile_develop.android.ui.controller.command;

import android.view.View;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;

/**
 * Created by chris on 23/05/2014.
 */
public class TitleBackCommandController extends CommandController {

    public TitleBackCommandController(
            ViewHolderFactory viewHolderFactory,
            ErrorHandler errorHandler,
            ThreadHelper threadHelper,
            CommandViewFactory viewFactory,
            int generalButtonContainerId,
            Integer backButtonContainerId,
            Integer moreButtonContainerId,
            Integer titleTextViewId,
            Integer noTitleViewId,
            CommandPopupMenuHandler popupMenuHandler
    ) {
        super(viewHolderFactory, errorHandler, threadHelper, viewFactory, generalButtonContainerId, backButtonContainerId, moreButtonContainerId, titleTextViewId, noTitleViewId, popupMenuHandler);
    }

    @Override
    public void start() throws Exception {
        super.start();
        View titleTextView = getChildView(this.titleTextViewId, false);
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Command backCommand = getModel().getBackCommand();
                if( backCommand != null ) {
                    backCommand.getAction().perform();
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
