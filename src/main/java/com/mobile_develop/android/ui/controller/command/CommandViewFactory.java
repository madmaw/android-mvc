package com.mobile_develop.android.ui.controller.command;

import com.mobile_develop.android.ui.Command;

import android.view.View;

public interface CommandViewFactory {

    public static class ViewAndId {

        private View view;
        private Integer id;

        public ViewAndId(View view, Integer id) {
            this.view = view;
            this.id = id;
        }

        View getView() {
            return this.view;
        }

        Integer getId() {
            return this.id;
        }
    }

	ViewAndId createView(Command command);

    ViewAndId createMoreView();
}
