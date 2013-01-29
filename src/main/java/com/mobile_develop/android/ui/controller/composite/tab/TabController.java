package com.mobile_develop.android.ui.controller.composite.tab;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.mobile_develop.android.ui.ErrorHandler;
import com.mobile_develop.android.ui.ThreadHelper;
import com.mobile_develop.android.ui.ViewHolderFactory;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerState;
import com.mobile_develop.android.ui.controller.Model;
import com.mobile_develop.android.ui.controller.composite.AbstractCompositeController;

import java.util.Map;

public class TabController<ModelType extends Model> extends AbstractCompositeController<ModelType> {

    private int containerViewId;
    private Map<Integer, Controller> tabViewIdsToControllers;
    protected Integer selectedViewId;

    public TabController(ViewHolderFactory viewHolderFactory, ErrorHandler errorHandler, ThreadHelper threadHelper, int containerViewId, Map<Integer, Controller> tabViewIdsToControllers) {
        super(viewHolderFactory, errorHandler, threadHelper, 1);
        this.containerViewId = containerViewId;
        this.tabViewIdsToControllers = tabViewIdsToControllers;
    }

    @Override
    protected ViewGroup getContainer(Controller controller) {
        return getChildView(containerViewId, true);
    }

    @Override
    public void start() throws Exception {
        super.start();
        for( final Integer tabViewId : tabViewIdsToControllers.keySet() ) {
            final View button = getChildView(tabViewId);
            if( button != null ) {
                if( button.isSelected() ) {
                    setContent(tabViewId);
                }
                button.setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                try {
                                    boolean b = view.isSelected();
                                    if (!b) {
                                        setContent(tabViewId);
                                    }
                                } catch (Exception ex) {
                                    handleError(ex);
                                }
                            }

                        }
                );
            }
        }
        if( selectedViewId != null ) {
            setContent(selectedViewId);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    protected void load(ModelType model) throws Exception {
        // do nothing
    }

    public void setContent(Integer selectedTabViewId) throws Exception {
        this.selectedViewId = selectedTabViewId;
        if( getState() == ControllerState.Started || getState() == ControllerState.Initialised ) {
            Controller selectedController = tabViewIdsToControllers.get(selectedTabViewId);
            for( final Integer tabViewId : tabViewIdsToControllers.keySet() ) {
                final View button = getChildView(tabViewId);
                if( button != null ) {
                    if( tabViewId.equals(selectedTabViewId)) {
                        if( !button.isSelected() ) {
                            button.setSelected(true);
                        }
                    } else {
                        if( button.isSelected() ) {
                            button.setSelected(false);
                        }
                    }
                }
            }
            if( !contains(selectedController) ) {
                // remove all the tabbed controllers
                for( Controller controller : tabViewIdsToControllers.values() ) {
                    if( contains(controller) ) {
                        remove(controller);
                    }
                }
                add(selectedController);
            }
        }
    }

    @Override
    public String getTitle() {
        String title;
        if( selectedViewId != null ) {
            Controller selectedController = tabViewIdsToControllers.get(selectedViewId);
            title = selectedController.getTitle();
        } else {
            title = super.getTitle();
        }
        return title;
    }
}
