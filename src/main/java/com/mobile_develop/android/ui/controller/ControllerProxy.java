package com.mobile_develop.android.ui.controller;

import android.view.View;
import android.view.ViewGroup;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 5/06/2014.
 */
public class ControllerProxy<ProxiedControllerType extends Controller> implements Controller {

    protected ProxiedControllerType proxied;

    private List<ControllerStateListener> stateListeners;
    private List<ControllerCommandListener> commandListeners;

    private ControllerStateListener stateListener;
    private ControllerCommandListener commandListener;

    public ControllerProxy() {
        this(null);
    }

    public ControllerProxy(ProxiedControllerType proxied) {
        this.proxied = proxied;
        this.stateListeners = new ArrayList<ControllerStateListener>();
        this.commandListeners = new ArrayList<ControllerCommandListener>();
        this.stateListener = new ControllerStateListener() {
            @Override
            public void stateChanged(Controller source, ControllerState from, ControllerState to) {
                fireStateChanged(from, to);
            }
        };
        this.commandListener = new ControllerCommandListener() {
            @Override
            public void commandsChanged(Controller controller, List<Command> commands) {
                fireCommandsChanged(commands);
            }
        };
    }

    public ProxiedControllerType getProxied() {
        return proxied;
    }

    public void setProxied(ProxiedControllerType proxied) {
        this.proxied = proxied;
    }

    protected void fireStateChanged(ControllerState from, ControllerState to) {
        for( ControllerStateListener stateListener : this.stateListeners ) {
            stateListener.stateChanged(this, from, to);
        }
    }

    protected void fireCommandsChanged(List<Command> commands) {
        for( ControllerCommandListener commandListener : this.commandListeners ) {
            commandListener.commandsChanged(this, commands);
        }
    }


    @Override
    public List<Command> getCommands() {
        return this.proxied.getCommands();
    }

    @Override
    public ViewHolder getViewHolder() {
        return this.proxied.getViewHolder();
    }

    @Override
    public ControllerState getState() {
        return this.proxied.getState();
    }

    @Override
    public void init(ViewGroup container, View reuseView, boolean attachToContainer) {
        this.proxied.init(container, reuseView, attachToContainer);
    }

    @Override
    public void start() throws Exception {
        this.proxied.start();
    }

    @Override
    public void stop() throws Exception {
        this.proxied.stop();
    }

    @Override
    public void destroy() {
        this.proxied.destroy();
    }

    @Override
    public String getTitle() {
        return this.proxied.getTitle();
    }

    @Override
    public void addStateListener(ControllerStateListener stateListener) {
        if( this.stateListeners.size() == 0 ) {
            this.proxied.addStateListener(this.stateListener);
        }
        this.stateListeners.add(stateListener);
    }

    @Override
    public void removeStateListener(ControllerStateListener stateListener) {
        this.stateListeners.remove(stateListener);
        if( this.stateListeners.size() == 0 ) {
            this.proxied.removeStateListener(stateListener);
        }
    }

    @Override
    public void addCommandListener(ControllerCommandListener commandListener) {
        if( this.commandListeners.size() == 0 ) {
            this.proxied.addCommandListener(commandListener);
        }
        this.commandListeners.add(commandListener);
    }

    @Override
    public void removeCommandListener(ControllerCommandListener commandListener) {
        this.commandListeners.remove(commandListener);
        if( this.commandListeners.size() == 0 ) {
            this.proxied.removeCommandListener(commandListener);
        }
    }
}
