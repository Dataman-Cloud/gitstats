package com.dataman.gitstats.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @ClassName: ClassUitl
 * @Description: TODO()
 * @author liuqing
 * @date 2017年6月6日 下午5:06:31
 * @Copyright © 2017北京数人科技有限公司
 */
public class ClassUitl {

	/**
	 * 利用反射实现对象之间相同属性复制 
	 * @param source 要复制的
	 * @param to 复制给
	 */
	public static <T> T copyProperties(Object source, T target) throws Exception {
		return copyPropertiesExclude(source, target, null);
	}

	/**
	 * 复制对象属性
	 * 
	 * @param from
	 * @param to
	 * @param excludsArray
	 *            排除属性列表
	 * @throws Exception
	 */
	public static <T> T copyPropertiesExclude(Object from, T to, String[] excludsArray) throws Exception {
		if (from==null){
			return null;
		}
		List<String> excludesList = null;
		if (excludsArray != null && excludsArray.length > 0) {
			excludesList = Arrays.asList(excludsArray); // 构造列表对象
		}
		List<Method> fromMethodarr = new ArrayList<Method>();
		for (Class<?> clazz = from.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			fromMethodarr.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		}
		// Method[] fromMethods = from.getClass().getDeclaredMethods();
		List<Method> toMethodarr = new ArrayList<Method>();
		for (Class<?> clazz = to.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			toMethodarr.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		}
		// Method[] toMethods = to.getClass().getDeclaredMethods();
		Method fromMethod = null, toMethod = null;
		String fromMethodName = null, toMethodName = null;

		for (int i = 0; i < fromMethodarr.size(); i++) {
			fromMethod = fromMethodarr.get(i);
			fromMethodName = fromMethod.getName();
			if (!fromMethodName.contains("get"))
				continue;
			// 排除列表检测
			if (excludesList != null && excludesList.contains(fromMethodName.substring(3).toLowerCase())) {

				continue;
			}
			toMethodName = "set" + fromMethodName.substring(3);
			toMethod = findMethodByName(toMethodarr, toMethodName);

			if (toMethod == null)
				continue;
			Object value = fromMethod.invoke(from, new Object[0]);

			if (value == null)
				continue;
			// 集合类判空处理
			if (value instanceof Collection) {
				Collection<?> newValue = (Collection<?>) value;
				if (newValue.size() <= 0)
					continue;
			}
			toMethod.invoke(to, new Object[] { value });
		}
		return to;
	}

	/**
	 * 对象属性值复制，仅复制指定名称的属性值
	 * 
	 * @param from
	 * @param to
	 * @param includsArray
	 * @throws Exception
	 */
	public static void copyPropertiesInclude(Object from, Object to, String[] includsArray) throws Exception {

		List<String> includesList = null;
		if (includsArray != null && includsArray.length > 0) {
			includesList = Arrays.asList(includsArray);
		} else {
			return;
		}
		List<Method> fromMethodarr = new ArrayList<Method>();
		for (Class<?> clazz = from.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			fromMethodarr.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		}
		// Method[] fromMethods = from.getClass().getDeclaredMethods();
		List<Method> toMethodarr = new ArrayList<Method>();
		for (Class<?> clazz = to.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			toMethodarr.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		}
		// Method[] toMethods = to.getClass().getDeclaredMethods();
		Method fromMethod = null, toMethod = null;
		String fromMethodName = null, toMethodName = null;

		for (int i = 0; i < fromMethodarr.size(); i++) {

			fromMethod = fromMethodarr.get(i);
			fromMethodName = fromMethod.getName();

			if (!fromMethodName.contains("get"))
				continue;

			// 排除列表检测
			String str = fromMethodName.substring(3);

			if (!includesList.contains(str.substring(0, 1).toLowerCase() + str.substring(1))) {
				continue;
			}

			toMethodName = "set" + fromMethodName.substring(3);
			toMethod = findMethodByName(toMethodarr, toMethodName);

			if (toMethod == null)
				continue;

			Object value = fromMethod.invoke(from, new Object[0]);

			if (value == null)
				continue;

			// 集合类判空处理
			if (value instanceof Collection) {

				Collection<?> newValue = (Collection<?>) value;

				if (newValue.size() <= 0)
					continue;
			}

			toMethod.invoke(to, new Object[] { value });
		}
	}

	/**
	 * 从方法数组中获取指定名称的方法
	 * 
	 * @param methods
	 * @param name
	 * @return
	 */
	public static Method findMethodByName(List<Method> methods, String name) {
		// System.out.println(name);
		Optional<Method> op= methods.stream().filter(method ->method.getName().equals(name)).findFirst();
		return op.isPresent() ? op.get() : null;
	}

}
