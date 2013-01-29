package com.mobile_develop.android.ui.controller.composite.list;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.AbstractController;
import com.mobile_develop.android.ui.controller.Model;

public class ListViewListController<ModelType extends Model, ControllerIdType> extends AbstractController<ModelType> {

	private ControllerListContext<ModelType, ControllerIdType> context;
	private ControllerListContextAdapter<ModelType, ControllerIdType> listAdapter;
	private int listViewId;
	
	public ListViewListController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, ControllerListContext<ModelType, ControllerIdType> context, int listViewId)
	{
		super(viewHolderFactory, errorHandler, threadHelper);
		this.context = context;
		this.listViewId = listViewId;
		this.listAdapter = new ControllerListContextAdapter<ModelType, ControllerIdType>(context, errorHandler);
	}
	
	@Override
	public void init(ViewGroup container, View reuse, boolean attachToContainer) {
		super.init(container, reuse, attachToContainer);
		ListView listView = getListView();
		listView.setRecyclerListener(
			new AbsListView.RecyclerListener() {
				@Override
				public void onMovedToScrapHeap(View view) {
					listAdapter.viewInactive(view);
				}
			}
		);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> source, View clicked, int row, long id) {
                try {
				    context.listItemSelected(row);
                } catch( Exception ex ) {
                    handleError(ex);
                }
			}
		});
	}
	
	@Override
	public void setModel(ModelType model) throws Exception
	{
		//this.context.setModel(model);
		super.setModel(model);
	}

	@Override
	protected void load(ModelType model) throws Exception {
        this.context.setModel(model);
		final ListView listView = getListView();
		if( listView != null && listView.getAdapter() != this.listAdapter ) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					listView.setAdapter(listAdapter);
				}
			};
			invokeAndWait(r);
		} else {
			// reload the list view
			listAdapter.notifyDataSetInvalidated();
		}
	}
	
	private ListView getListView()
	{
		return this.getChildView(this.listViewId, true);
	}

    @Override
    public String getTitle() {

        return null;
    }
}
