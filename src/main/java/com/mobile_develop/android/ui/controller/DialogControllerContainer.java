package com.mobile_develop.android.ui.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.mobile_develop.android.ui.Action;
import com.mobile_develop.android.ui.Command;
import com.mobile_develop.android.ui.ErrorHandler;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 19/12/11
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogControllerContainer {

    public static final AlertDialog embed(Context context, final ErrorHandler errorHandler, final Controller controller, final CharSequence cancelButtonText, Drawable icon) {
        return embed(context, errorHandler, controller, cancelButtonText, icon, null);
    }

    public static final AlertDialog embed(Context context, final ErrorHandler errorHandler, final Controller controller, final CharSequence cancelButtonText, Drawable icon, Integer themeId) {
        // check the version

        AlertDialog.Builder dialogBuilder;
        if( themeId != null ) {
            try {
                if( AlertDialog.Builder.class.getConstructor(Context.class, Integer.TYPE) != null ) {
                    dialogBuilder = new AlertDialog.Builder(context, themeId);
                } else {
                    dialogBuilder = null;
                }
            } catch( Exception ex ) {
                dialogBuilder = null;
            }
            if( dialogBuilder == null ) {
                dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, themeId));
            }
        } else {
            dialogBuilder = new AlertDialog.Builder(context);
        }

        dialogBuilder.setCancelable(true);

        dialogBuilder.setTitle(" ");
        if( icon != null ) {
            dialogBuilder.setIcon(icon);
        }
        controller.init(null, null, false);
        dialogBuilder.setView(controller.getViewHolder().getViews().get(0));


        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // populate the dialog
                try {
                    controller.start();
                } catch (Exception ex) {
                    errorHandler.handleError(
                            AbstractController.DEFAULT_INTERNAL_ERROR_TYPE,
                            AbstractController.DEFAULT_INTERNAL_ERROR_MESSAGE,
                            ex
                    );
                }
                // TODO handle the commands in this controller
                String title = controller.getTitle();
                dialog.setTitle(title);
            }
        });
        if( cancelButtonText != null ) {
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, cancelButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        final int maxButtonIndex = cancelButtonText != null ? 2 : 3;
        loadCommands(dialog, controller, maxButtonIndex);
        // not sure if reloading the command buttons is going to work well
        controller.addCommandListener(new ControllerCommandListener() {
            @Override
            public void commandsChanged(Controller controller, List<Command> commands) {
                loadCommands(dialog, controller, maxButtonIndex);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    controller.stop();
                } catch( Exception ex ) {
                    errorHandler.handleError(
                            AbstractController.DEFAULT_INTERNAL_ERROR_TYPE,
                            AbstractController.DEFAULT_INTERNAL_ERROR_MESSAGE,
                            ex
                    );
                }
                controller.destroy();
            }
        });
        return dialog;
    }

    private static void loadCommands(AlertDialog dialog, Controller controller, int maxButtonIndex) {
        List<Command> commands = controller.getCommands();
        if( commands != null ) {
            int buttonIndex = 0;
            for( int i=0; i<commands.size() && buttonIndex < 3; i++ ) {
                Command command = commands.get(i);
                Integer buttonId;
                switch( buttonIndex ) {
                    case 0:
                        buttonId = AlertDialog.BUTTON_POSITIVE;
                        break;
                    case 1:
                        buttonId = AlertDialog.BUTTON_NEUTRAL;
                        break;
                    case 2:
                        buttonId = AlertDialog.BUTTON_NEGATIVE;
                        break;
                    default:
                        buttonId = null;
                        break;
                }
                if( buttonId != null ) {
                    if( command.isEnabled() ) {

                        String name;
                        name = command.getType().getName();
                        final Action action = command.getAction();
                        Button button = dialog.getButton(buttonId);
                        if( button != null ) {
                            button.setEnabled(true);
                            button.setText(name);
                            View.OnClickListener onClickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    action.perform();
                                }
                            };
                            button.setOnClickListener(onClickListener);
                        } else {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    action.perform();
                                }
                            };
                            dialog.setButton(buttonId, name, onClickListener);
                        }
                    } else {
                        Button button = dialog.getButton(buttonId);
                        if( button != null ) {
                            button.setEnabled(false);
                        }
                    }
                }
                buttonIndex++;
                if( buttonIndex > maxButtonIndex ) {
                    break;
                }
            }
        }
    }
}
