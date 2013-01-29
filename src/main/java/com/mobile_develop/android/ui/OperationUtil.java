package com.mobile_develop.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import java.lang.reflect.Method;
import java.text.NumberFormat;

public class OperationUtil {

    public static final <ResultType> void perform(
            Activity activity,
            final ThreadHelper threadHelper,
            final ErrorHandler errorHandler,
            final Operation<ResultType> operation,
            String title,
            String message,
            final String errorTitle
    ) {
        int style;
        if( operation.reportsProgress() ) {
            style = ProgressDialog.STYLE_HORIZONTAL;
        } else {
            style = ProgressDialog.STYLE_SPINNER;
        }
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(operation.reportsProgress());
        progressDialog.setProgressStyle(style);
        // attempt to turn off the (redundant) progress text
        try {
            Method method = progressDialog.getClass().getMethod("setProgressNumberFormat", String.class);
            method.invoke(progressDialog, (Object)null);
        } catch( Exception ex ) {
            // oh well
        }
        try {
            Method method = progressDialog.getClass().getMethod("setProgressPercentFormat", NumberFormat.class);
            method.invoke(progressDialog, (Object)null);
        } catch( Exception ex ) {
            // meh
        }
        progressDialog.show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    final ResultType result;
                    //  expose cancel and progress info
                    progressDialog.setCancelable(operation.cancelable());
                    progressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            operation.cancel();
                        }
                    });
                    progressDialog.setOnDismissListener(new ProgressDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            operation.cancel();
                        }
                    });
                    try {
                        result = operation.perform(new OperationObserver() {
                            @Override
                            public void progressChanged(final Integer value, final Integer maxValue, final String description) {
                                threadHelper.invoke(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean indeterminate = value == null || maxValue == null;
                                        progressDialog.setIndeterminate(indeterminate);
                                        if( !indeterminate ) {
                                            progressDialog.setMax(maxValue);
                                            progressDialog.setProgress(value);
                                        }
                                        progressDialog.setMessage(description);
                                    }
                                });
                            }

                            @Override
                            public void cancelableChanged(boolean cancelable) {
                                progressDialog.setCancelable(cancelable);
                            }
                        });
                    } finally {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        };
                        threadHelper.invoke(r);

                    }
                    threadHelper.invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                operation.performInMain(result);
                            }
                        }
                    );
                } catch( final Exception ex ) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            errorHandler.handleError(
                                    errorTitle,
                                    ex.getMessage(),
                                    ex
                            );

                        }
                    };
                    threadHelper.invokeLater(r);
                }
            }
        };
        threadHelper.threadExpensiveOperation(r);

    }

}
