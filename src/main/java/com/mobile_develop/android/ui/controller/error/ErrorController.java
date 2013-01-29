package com.mobile_develop.android.ui.controller.error;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.AbstractController;

public class ErrorController extends AbstractController<ErrorModel> {

	private int viewIdImage;
	private int viewIdText;
	private int viewIdDetail;
    private Integer viewIdMoreDetailButton;
    private Integer viewIdType;
	
	private Drawable[] errorLevelBitmaps;
    private boolean scrollDetail;
	
	public ErrorController(
			ViewHolderFactory viewHolderFactory, 
			ThreadHelper threadHelper,
            Integer viewIdType,
            int viewIdImage,
			int viewIdText,
			int viewIdDetail,
            Integer viewIdMoreDetailButton,
			Drawable[] errorLevelBitmaps,
            boolean scrollDetail
	)
	{
		super(viewHolderFactory, null, threadHelper);
		this.viewIdImage = viewIdImage;
        this.viewIdType = viewIdType;
		this.viewIdText = viewIdText;
		this.viewIdDetail = viewIdDetail;
        this.viewIdMoreDetailButton = viewIdMoreDetailButton;
		this.errorLevelBitmaps = errorLevelBitmaps;
        this.scrollDetail = scrollDetail;
	}

    @Override
    public void init(ViewGroup container, View reuseView, boolean attachToContainer) {
        super.init(container, reuseView, attachToContainer);
        TextView detailView = getChildView(viewIdDetail, false);
        if( detailView != null && scrollDetail ) {
            detailView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    @Override
    protected void attachListeners() {
        super.attachListeners();
        if( this.viewIdMoreDetailButton != null ) {
            Button moreDetailButton = getChildView(this.viewIdMoreDetailButton, true);
            moreDetailButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    getModel().requestMoreDetail();
                }
            });
        }
    }

    @Override
    protected void detachListeners() {
        super.detachListeners();
    }

    @Override
	protected void load(ErrorModel model) throws Exception {
        if( this.viewIdType != null ) {
            TextView textView = this.getChildView(this.viewIdType, true);
            textView.setText(model.getErrorType());
        }
		ImageView imageView = this.getChildView(viewIdImage, false);
		if( imageView != null ) 
		{
			// look up the image
			ErrorModel.ErrorLevel errorLevel = model.getErrorLevel();
			if( errorLevel != null && errorLevelBitmaps != null )
			{
				imageView.setImageDrawable(errorLevelBitmaps[errorLevel.ordinal()]);
			} else {
				imageView.setImageBitmap(null);
			}
		}
		TextView textView = this.getChildView(viewIdText, false);
		if( textView != null ) 
		{
			textView.setText(model.getMessage());
		}
		TextView detailView = this.getChildView(viewIdDetail, false);
		if( detailView != null ) 
		{
			detailView.setText(model.getDetail());
		}	
	}


    @Override
    public String getTitle() {
        ErrorModel model = getModel();
        if( model != null ) {
            String errorType = model.getErrorType();
            if( errorType != null ) {
                return errorType;
            } else {
                ErrorModel.ErrorLevel errorLevel = model.getErrorLevel();
                if( errorLevel != null ) {
                    return errorLevel.name();
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}
