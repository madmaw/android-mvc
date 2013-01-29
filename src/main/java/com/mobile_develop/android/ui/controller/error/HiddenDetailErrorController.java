package com.mobile_develop.android.ui.controller.error;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 20/12/11
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class HiddenDetailErrorController extends ErrorController {

    private int viewIdMoreDetailButton;
    private int viewIdMoreDetailPanel;
    private int viewIdLessDetailButton;
    private int viewIdLessDetailPanel;

    public HiddenDetailErrorController(
            ViewHolderFactory viewHolderFactory,
            ThreadHelper threadHelper,
            Integer viewIdType,
            int viewIdImage,
            int viewIdText,
            int viewIdDetail,
            Drawable[] errorLevelBitmaps,
            boolean scrollDetail,
            int viewIdMoreDetailButton,
            int viewIdMoreDetailPanel,
            int viewIdLessDetailButton,
            int viewIdLessDetailPanel
    ) {
        super(viewHolderFactory, threadHelper, viewIdType, viewIdImage, viewIdText, viewIdDetail, null, errorLevelBitmaps, scrollDetail);

        this.viewIdMoreDetailButton = viewIdMoreDetailButton;
        this.viewIdMoreDetailPanel = viewIdMoreDetailPanel;
        this.viewIdLessDetailButton = viewIdLessDetailButton;
        this.viewIdLessDetailPanel = viewIdLessDetailPanel;
    }

    @Override
    protected void attachListeners() {
        super.attachListeners();
        // listen for more/less requests
        Button lessDetailButton = this.getChildView(viewIdLessDetailButton, false);
        if( lessDetailButton != null ) {
            lessDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDetail();
                }
            });
        }
        Button moreDetailButton = this.getChildView(viewIdMoreDetailButton, false);
        if( moreDetailButton != null ) {
            moreDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDetail();
                }
            });
        }
    }

    @Override
    protected void load(ErrorModel model) throws Exception {
        super.load(model);

        boolean detailAvailable = model.isDetailAvailable();
        Button lessDetailButton = this.getChildView(viewIdLessDetailButton, false);
        if( lessDetailButton != null ) {
            if( !detailAvailable ) {
                lessDetailButton.setVisibility(View.GONE);
            } else {
                lessDetailButton.setVisibility(View.VISIBLE);
            }
        }
        Button moreDetailButton = this.getChildView(viewIdMoreDetailButton, false);
        if( moreDetailButton != null ) {
            if( !detailAvailable ) {
                moreDetailButton.setVisibility(View.GONE);
            } else {
                moreDetailButton.setVisibility(View.VISIBLE);
            }
        }
        View lowDetail = getChildView(viewIdLessDetailPanel, false);
        if( lowDetail != null ) {
            if( !detailAvailable ) {
                lowDetail.setVisibility(View.GONE);
            } else {
                lowDetail.setVisibility(View.VISIBLE);
            }
        }
        View highDetail = getChildView(viewIdMoreDetailPanel, false);
        if( highDetail != null ) {
            if( !detailAvailable ) {
                highDetail.setVisibility(View.GONE);
            } else {
                highDetail.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void detachListeners() {
        super.detachListeners();
        Button lessDetailButton = this.getChildView(viewIdLessDetailButton, false);
        if( lessDetailButton != null ) {
            lessDetailButton.setOnClickListener(null);
        }
        Button moreDetailButton = this.getChildView(viewIdMoreDetailButton, false);
        if( moreDetailButton != null ) {
            moreDetailButton.setOnClickListener(null);
        }
    }

    public void showDetail() {
        View lowDetail = getChildView(viewIdLessDetailPanel, true);
        View highDetail = getChildView(viewIdMoreDetailPanel, true);
        lowDetail.setVisibility(View.INVISIBLE);
        highDetail.setVisibility(View.VISIBLE);
    }

    public void hideDetail() {
        View lowDetail = getChildView(viewIdLessDetailPanel, true);
        View highDetail = getChildView(viewIdMoreDetailPanel, true);
        lowDetail.setVisibility(View.VISIBLE);
        highDetail.setVisibility(View.INVISIBLE);

    }
}
