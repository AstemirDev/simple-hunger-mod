package ru.astemir.simplehunger.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Traverse<T> {

	private final Map<String, Field> fieldCache = new HashMap<>();
	private final Map<String, Method> methodCache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <R> R invoke(T target, String methodName, Object... args) {
		Class<?> clazz = target.getClass();
		String key = clazz.getName() + "#" + methodName;

		Method method = methodCache.get(key);
		if (method == null) {
			try {
				method = getMethodRecursive(clazz, methodName, ReflectionUtils.getArgs(args));
				method.setAccessible(true);
				methodCache.put(key, method);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Method not found: " + methodName, e);
			}
		}

		try {
			return (R) method.invoke(target, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Method getMethodRecursive(Class<?> clazz, String name, Class<?>[] args) throws NoSuchMethodException {
		try {
			return clazz.getDeclaredMethod(name, args);
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && superClass != Object.class) {
				return getMethodRecursive(superClass, name, args);
			}
			throw e;
		}
	}

	public <V> void set(T target, String fieldName, V value) {
		Field field = getFieldCached(target.getClass(), fieldName);
		try {
			field.set(target, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <V> V get(T target, String fieldName) {
		Field field = getFieldCached(target.getClass(), fieldName);
		try {
			return (V) field.get(target);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private Field getFieldCached(Class<?> clazz, String fieldName) {
		String key = clazz.getName() + "#" + fieldName;
		return fieldCache.computeIfAbsent(key, k -> {
			try {
				Field field = getFieldRecursive(clazz, fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private Field getFieldRecursive(Class<?> clazz, String name) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && superClass != Object.class) {
				return getFieldRecursive(superClass, name);
			}
			throw e;
		}
	}
}