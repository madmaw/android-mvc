package com.mobile_develop.android.ui.controller.composite.async;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.mobile_develop.android.ui.controller.AbstractModel;
import com.mobile_develop.android.ui.controller.ModelListener;
import com.mobile_develop.android.ui.controller.error.ErrorListModel;
import com.mobile_develop.android.ui.controller.error.ErrorModel;

public class AsynchronousStartErrorListModel extends AbstractModel implements ErrorListModel {

	private List<ErrorModel> errorModels;
    private AsynchronousStartController<?> owner;
	
	public AsynchronousStartErrorListModel(AsynchronousStartController<?> owner){
		this.errorModels = new ArrayList<ErrorModel>();
        this.owner = owner;
	}

	public void addError(final ErrorModel.ErrorLevel errorLevel, final String errorType, final String message, final Throwable cause)
	{
		ErrorModel errorModel = new ErrorModel(){

			@Override
			public void addModelListener(ModelListener listener) {
				// never changes, ignore listeners
			}

			@Override
			public void removeModelListener(ModelListener listener) {
				
			}

            @Override
            public String getErrorType() {
                return errorType;
            }

            @Override
			public ErrorLevel getErrorLevel() {
				return errorLevel;
			}

			@Override
			public String getMessage() {
				return message;
			}

			@Override
			public String getDetail() {
                if( cause != null ) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    cause.printStackTrace(pw);
                    pw.flush();
                    return sw.toString();
                } else {
                    return null;
                }
			}

            @Override
            public void requestMoreDetail() {
                owner.displayMoreErrorDetail(errorType, message, cause);
            }

            @Override
            public boolean isDetailAvailable() {
                return cause != null;
            }
        };
		errorModels.add(0, errorModel);
		this.fireModelChangeEvent(CHANGE_TYPE_ERROR_ADDED, errorModel);
	}
	
	public void clearErrors()
	{
		this.errorModels.clear();
		this.fireModelChangeEvent(CHANGE_TYPE_ALL, null);
	}
	
	@Override
	public int getNumberOfErrors() {
		return errorModels.size();
	}

	@Override
	public ErrorModel getErrorModel(int index) {
		return errorModels.get(index);
	}

    @Override
    public void requestRetry() {
        owner.restart();
    }
}
