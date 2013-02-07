package com.mobile_develop.android.ui.controller.command;

import android.view.View;
import com.mobile_develop.android.ui.Command;

import java.util.List;

public interface CommandPopupMenuHandler {
    void showPopupMenu(View view, List<Command> commands);
}
