package com.lu.lib_annotation;

import android.app.Activity;
import android.view.View;

/**
 * @author Luliang
 * @version v1.0
 * @date 2020/5/20
 * @description
 */
public class Utils {
	public static View findViewById(Activity activity, int id) {
		return activity.findViewById(id);
	}
}
