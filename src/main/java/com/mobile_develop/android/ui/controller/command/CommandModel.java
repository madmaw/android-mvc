package com.mobile_develop.android.ui.controller.command;

import java.util.List;

import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.controller.Model;

public interface CommandModel extends Model {
	
	public static final int CHANGE_TYPE_COMMANDS = 0;
	
	List<Command> getHighPriorityCommands();

    List<Command> getAllCommands();
	
	Command getBackCommand();

    String getTitle();

    boolean shouldShowMoreOption();

    void requestShowMore();
}
