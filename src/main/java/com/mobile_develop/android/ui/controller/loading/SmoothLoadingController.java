package com.mobile_develop.android.ui.controller.loading;

import android.widget.ProgressBar;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;

public class SmoothLoadingController extends LoadingController {

    private boolean running;
    private long refreshRate;

    public SmoothLoadingController(ViewHolderFactory viewHolderFactory, ThreadHelper threadHelper, int viewIdProgress, int viewIdMessage, int granularity, long refreshRate) {
        super( viewHolderFactory, threadHelper, viewIdProgress, viewIdMessage, granularity );
        this.refreshRate = refreshRate;
    }

    @Override
    protected void load(LoadingModel model) throws Exception {
        super.load(model);
        final ProgressBar progressBar = getChildView(viewIdProgress);
        if( progressBar != null ) {
            Integer currentStep = model.getCurrentStep();
            Integer maxStep = model.getMaxStep();
            synchronized (this) {
                this.notify();
            }
            if( currentStep != null && maxStep != null ) {
                if( currentStep.intValue() >= maxStep.intValue() ) {
                    running = false;
                } else {
                    if( !running ) {
                        running = true;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                long rate = refreshRate;
                                while( running ) {
                                    synchronized (SmoothLoadingController.this) {
                                        try {
                                            SmoothLoadingController.this.wait(rate);
                                        } catch( Exception ex ) {
                                            // just ignore
                                        }
                                    }
                                    Integer currentStep = getModel().getCurrentStep();
                                    Integer maxStep = getModel().getMaxStep();
                                    if( currentStep != null && maxStep != null && currentStep < maxStep ) {
                                        int progress = progressBar.getProgress();
                                        if( progress == currentStep * granularity ) {
                                            rate = refreshRate;
                                        } else {
                                            rate += refreshRate;
                                        }
                                        final int value = progress + 1;
                                        if( value < (currentStep+1) * granularity ) {
                                            threadHelper.invokeAndWait(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressBar.setProgress(value);
                                                        }
                                                    }
                                            );
                                        }
                                    } else {
                                        running = false;
                                    }

                                }
                            }
                        };
                        thread.start();
                    }
                }
            } else {
                running = false;
            }
        }

    }
}
