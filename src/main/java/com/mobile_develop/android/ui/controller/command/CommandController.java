package com.mobile_develop.android.ui.controller.command;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import android.widget.PopupMenu;
import android.widget.TextView;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.AbstractController;

public class CommandController extends AbstractController<CommandModel> {

	protected CommandViewFactory viewFactory;
    protected Integer backButtonContainerId;
    protected Integer moreButtonContainerId;
    protected int generalButtonContainerId;
    protected Integer titleTextViewId;
    protected Integer noTitleViewId;
    protected CommandPopupMenuHandler popupMenuHandler;
	
	public CommandController(
            ViewHolderFactory viewHolderFactory,
            ErrorHandler errorHandler,
            ThreadHelper threadHelper,
            CommandViewFactory viewFactory,
            int generalButtonContainerId,
            Integer backButtonContainerId,
            Integer moreButtonContainerId,
            Integer titleTextViewId,
            Integer noTitleViewId,
            CommandPopupMenuHandler popupMenuHandler) {
		super(viewHolderFactory, errorHandler, threadHelper);
		this.viewFactory = viewFactory;
		this.backButtonContainerId = backButtonContainerId;
        this.moreButtonContainerId = moreButtonContainerId;
		this.generalButtonContainerId = generalButtonContainerId;
        this.titleTextViewId = titleTextViewId;
        this.noTitleViewId = noTitleViewId;
        this.popupMenuHandler = popupMenuHandler;
	}

	@Override
	protected void load(CommandModel model) throws Exception {
		if( backButtonContainerId != null ) {
			ViewGroup viewGroup = getChildView(backButtonContainerId);
            if( viewGroup != null ) {
                viewGroup.removeAllViews();
                Command backCommand = model.getBackCommand();
                attachView(backCommand, viewGroup);
            }
		}
        if( this.moreButtonContainerId != null ) {
            ViewGroup viewGroup = getChildView(moreButtonContainerId);
            if( viewGroup != null ) {
                viewGroup.removeAllViews();
                CommandViewFactory.ViewAndId moreViewAndId;
                if( model.shouldShowMoreOption() && (moreViewAndId = this.viewFactory.createMoreView()) != null ) {
                    View moreView = moreViewAndId.getView();
                    View moreButton;
                    if( moreViewAndId.getId() != null ) {
                        moreButton = moreView.findViewById(moreViewAndId.getId());
                    } else {
                        moreButton = moreView;
                    }
                    moreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do we support popups?
                            popupMenuHandler.showPopupMenu(v, getModel().getAllCommands());
                        }
                    });
                    viewGroup.addView(moreView);
                }
            }
        }
        ViewGroup viewGroup = getChildView(this.generalButtonContainerId);
        if( viewGroup != null ) {
            viewGroup.removeAllViews();
            List<Command> generalCommands = model.getHighPriorityCommands();
            if( generalCommands != null ) {
                for( int i=0; i<generalCommands.size(); i++ ) {
                    Command generalCommand = generalCommands.get( i );
                    attachView(generalCommand, viewGroup);
                }
            }
        }
        if( titleTextViewId != null ) {
            TextView titleTextView = getChildView(titleTextViewId);
            if( titleTextView != null ) {
                String title = model.getTitle();
                titleTextView.setText(title);
                if( noTitleViewId != null ) {
                    View noTitleView = getChildView(noTitleViewId, true);
                    int noTitleViewVisibility;
                    if( title != null ) {
                        noTitleViewVisibility = View.GONE;
                    } else {
                        noTitleViewVisibility = View.VISIBLE;
                    }
                    noTitleView.setVisibility(noTitleViewVisibility);
                }

            }
        }
	}

    @Override
    public String getTitle() {
        //return getModel().getTitle();
        // we eat the title, otherwise you get infinite recursion
        return null;
    }

    private boolean attachView( final Command command, ViewGroup container ) {
		boolean result;
		if( command != null ) {
			CommandViewFactory.ViewAndId viewAndId = viewFactory.createView(command);
			if( viewAndId != null ) {
                View view = viewAndId.getView();
                View button;
                if( viewAndId.getId() != null ) {
                    button = view.findViewById(viewAndId.getId());
                } else {
                    button = view;
                }
				button.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						command.getAction().perform();
					}
				});
				container.addView(view);
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}
}
