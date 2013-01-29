package com.mobile_develop.android.ui.controller.loading;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.AbstractController;

public class LoadingController extends AbstractController<LoadingModel> {
	
	protected int viewIdProgress;
	private int viewIdMessage;
    protected int granularity;

    public LoadingController(ViewHolderFactory viewHolderFactory, ThreadHelper threadHelper, int viewIdProgress, int viewIdMessage) {
        this(viewHolderFactory, threadHelper, viewIdProgress, viewIdMessage, 1);
    }

    public LoadingController(ViewHolderFactory viewHolderFactory, ThreadHelper threadHelper, int viewIdProgress, int viewIdMessage, int granularity)
	{
		super(viewHolderFactory, null, threadHelper);
		this.viewIdProgress = viewIdProgress;
		this.viewIdMessage = viewIdMessage;
        this.granularity = granularity;
	}

	@Override
	protected void load(final LoadingModel model) throws Exception {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				ProgressBar progress = getChildView(viewIdProgress, false);
				if( progress != null ) 
				{
					Integer step = model.getCurrentStep();
					Integer maxStep = model.getMaxStep();
					if( step != null && maxStep != null ) 
					{
						progress.setProgress(step * granularity);
						progress.setMax(maxStep * granularity);
						progress.setIndeterminate(false);
					}
					else
					{
						progress.setIndeterminate(true);
					}
				}
				TextView message = getChildView(viewIdMessage, false);
				if( message != null ) 
				{
					message.setText(model.getMessage());
				}				
			}
		};
		invokeAndWait(r);
	}

    @Override
    public String getTitle() {
        // TODO make this configurable
        return "Loading...";
    }
}
