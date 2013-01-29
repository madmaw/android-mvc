package com.mobile_develop.android.ui.controller.command;

import android.app.Activity;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.controller.AbstractModel;
import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.ControllerCommandListener;

import java.util.ArrayList;
import java.util.List;

public class ControllerCommandModel extends AbstractModel implements CommandModel, ControllerCommandListener {

    private Integer maxBarButtons;
    private Controller source;
    private Activity activity;

    public ControllerCommandModel(Controller source, Activity activity, Integer maxBarButtons) {
        this.source = source;
        this.activity = activity;
        this.maxBarButtons = maxBarButtons;
    }

    @Override
    protected void startedListening() throws Exception {
        super.startedListening();
        this.source.addCommandListener(this);
    }

    @Override
    protected void stoppedListening() throws Exception {
        super.stoppedListening();
        this.source.removeCommandListener(this);
    }

    @Override
    public List<Command> getHighPriorityCommands() {
        Integer numButtons = maxBarButtons;
        if (numButtons != null) {
            if (shouldShowMoreOption()) {
                numButtons--;
            }
        }
        return getCommands(numButtons, false, false);
    }

    @Override
    public List<Command> getAllCommands() {
        return getCommands(null, true, true);
    }

    private List<Command> getCommands(Integer max, boolean includeBack, boolean includeSecret) {
        List<Command> commands = source.getCommands();
        ArrayList<Command> result;
        if (commands != null) {
            result = new ArrayList<Command>(commands.size());
            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);
                if ((command.getStyle() != Command.CommandStyle.Back || includeBack) && (command.getStyle() != Command.CommandStyle.Secret || includeSecret)) {
                    result.add(command);
                    if (max != null && result.size() >= max) {
                        break;
                    }
                }
            }
        } else {
            result = null;
        }
        return result;

    }

    @Override
    public Command getBackCommand() {
        Command backCommand = null;
        List<Command> commands = source.getCommands();
        if (commands != null) {
            for (int i = 0; i < commands.size(); i++) {
                Command command = commands.get(i);
                if (command.getStyle() == Command.CommandStyle.Back) {
                    backCommand = command;
                    break;
                }
            }
        }
        return backCommand;
    }

    @Override
    public void commandsChanged(Controller controller,
                                List<Command> commands) {
        fireModelChangeEvent(CHANGE_TYPE_COMMANDS, null);
    }

    @Override
    public String getTitle() {
        return source.getTitle();
    }

    @Override
    public boolean shouldShowMoreOption() {
        return (this.getCommands(null, false, false).size() >= maxBarButtons);
    }

    @Override
    public void requestShowMore() {
        // default behavior
        activity.openOptionsMenu();
    }
}
