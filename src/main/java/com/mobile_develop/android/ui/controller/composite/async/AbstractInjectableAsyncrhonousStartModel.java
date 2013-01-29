package com.mobile_develop.android.ui.controller.composite.async;

import com.mobile_develop.android.ui.controller.Model;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 7/02/12
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractInjectableAsyncrhonousStartModel<ModelType extends Model, InjectionType> extends AbstractAsynchronousStartModel<ModelType> {
    private InjectionType injection;

    public InjectionType getInjection() {
        return injection;
    }

    public void setInjection(InjectionType injection) {
        this.injection = injection;

    }
}
