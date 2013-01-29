package com.mobile_develop.android.ui.controller;

import java.util.List;

import com.mobile_develop.android.ui.Command;

public interface ControllerCommandListener {
	void commandsChanged(Controller controller, List<Command> commands);
}
