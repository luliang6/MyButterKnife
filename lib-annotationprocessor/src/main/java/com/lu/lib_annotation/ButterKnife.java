package com.lu.lib_annotation;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * @author Luliang
 * @version v1.0
 * @date 2020/5/18
 * @description
 */
public class ButterKnife {

	/**
	 * @param activity
	 */
	public static void bind(Activity activity) {
		if (activity != null) {
			try {
				Class<?> bindClass = Class.forName(activity.getClass().getCanonicalName() + "_ViewBinding");
				Class<?> activityClass = Class.forName(activity.getClass().getCanonicalName());

				// 这里传入activityClass，是为了区分重载的构造函数
				Constructor<?> constructor = bindClass.getDeclaredConstructor(activityClass);
				constructor.newInstance(activity);

			} catch (Exception e) {
				e.printStackTrace();
			}


		}

	}
}
