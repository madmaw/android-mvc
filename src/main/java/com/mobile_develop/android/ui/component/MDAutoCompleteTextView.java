package com.mobile_develop.android.ui.component;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 11/01/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class MDAutoCompleteTextView extends AutoCompleteTextView {

    private boolean userChanged;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            autoPopulate();
        }

        @Override
        public void onInvalidated() {
            autoPopulate();
        }
    };

    public MDAutoCompleteTextView(Context context) {
        super(context);
    }

    public MDAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MDAutoCompleteTextView(Context context, AttributeSet attrs, int s) {
        super(context, attrs, s);
    }

    @Override
    public boolean enoughToFilter() {
        if( getThreshold() == 0 ) {
            return true;
        } else {
            return super.enoughToFilter();
        }
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        ListAdapter oldAdapter = getAdapter();
        if( oldAdapter != null ) {
            oldAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        super.setAdapter(adapter);
        if( adapter != null ) {
            adapter.registerDataSetObserver(dataSetObserver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        userChanged = false;
        return super.onKeyDown(keyCode, event);
    }

    protected void autoPopulate() {
        // only want to auto-populate when adapter has changed in response to external events, not the user entering data
        ListAdapter adapter = getAdapter();
        CharSequence text = getText();
        if( !userChanged && (text == null || text.length() == 0) ) {
            if( adapter != null ) {
                if( adapter.getCount() == 1 ) {
                    CharSequence s = convertSelectionToString(adapter.getItem(0));
                    setText(s);
                }
            }
        }
    }
}
