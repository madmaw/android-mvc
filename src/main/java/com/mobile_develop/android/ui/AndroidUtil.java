package com.mobile_develop.android.ui;

import java.lang.reflect.Method;

public class AndroidUtil {
	
	public static boolean methodExists(Object o, String methodName, Class<?>...parameterTypes) {
		return methodExists(o.getClass(), methodName, parameterTypes);
	}
	
	public static boolean methodExists(Class<?> c, String methodName, Class<?>...parameterTypes) {
		try {
			Method m = c.getMethod(methodName, parameterTypes);
			return m != null;
		} catch( NoSuchMethodException ex ) {
			return false;
		}
	}
}
