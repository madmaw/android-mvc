package com.mobile_develop.android.ui.controller.command;

import android.app.Activity;
import android.view.View;
import com.mobile_develop.android.ui.Command;

import java.util.List;

public class SimpleCommandPopupMenuManager implements CommandPopupMenuHandler {

    private Activity activity;

    public SimpleCommandPopupMenuManager(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void showPopupMenu(View view, List<Command> commands) {
        // cheat: assume that the options menu uses all these comments
        activity.openOptionsMenu();
    }
}
