package com.lu.lib_reflection;

import android.app.Activity;

import java.lang.reflect.Field;

/**
 * @author Luliang
 * @version v1.0
 * @date 2020/5/18
 * @description
 */
public class ButterKnife {

	/**
	 * 反射来实现获取注解
	 *
	 * @param activity
	 */
	public static void bind(Activity activity) {
		if (activity != null) {
			for (Field field : activity.getClass().getDeclaredFields()) {
				BindView bindView = field.getAnnotation(BindView.class);
				if (bindView != null) {
					try {
						final int viewId = bindView.value();
						field.setAccessible(true);
						field.set(activity, activity.findViewById(viewId));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}


			}
		}
	}
}
