package com.mobile_develop.android.ui.controller.composite.factory;

import com.mobile_develop.android.ui.controller.Controller;
import com.mobile_develop.android.ui.controller.Model;

public interface FactoryModel extends Model {
	Controller createController() throws Exception;
}
