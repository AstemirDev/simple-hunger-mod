package ru.astemir.simplehunger.util;


import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionUtils {

    public static Class<?> getClass(String name){
	    try {
		    return Class.forName(name);
	    } catch (ClassNotFoundException e) {
		    throw new RuntimeException(e);
	    }
    }

    public static Field getField(@NonNullDecl Class<?> clazz, @NonNullDecl String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass == null ? null : getField(superClass, fieldName);
        }
    }

    public static boolean setFieldValue(@NonNullDecl Object of, @NonNullDecl String fieldName, @Nullable Object value) {
        try {
            boolean isStatic = of instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) of : of.getClass();

            Field field = getField(clazz, fieldName);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(isStatic ? null : of, value);
            return true;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    public static <T extends Annotation> Method getAnnotatedMethod(Class<?> type, Class<T> annotation, Predicate<T> predicate) {
        Class<?> clazz = type;
        while (clazz != Object.class) {
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    if (predicate.test(method.getAnnotation(annotation))) {
                        return method;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

	public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
	        allFields.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        return allFields;
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

    public static <T> T newInstance(Class<T> className, Object... args){
        try {
            return className.getDeclaredConstructor(getArgs(args)).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace(System.err);
        }
	    return null;
    }

    public static Class<?>[] getArgs(Object... args){
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = getArgType(args[i]);
        }
        return argTypes;
    }

    public static Class<?> getArgType(Object arg) {
	    return switch (arg) {
		    case null -> Object.class;
		    case Integer i -> int.class;
		    case Long l -> long.class;
		    case Boolean b -> boolean.class;
		    case Double v -> double.class;
		    case Float v -> float.class;
		    case Byte b -> byte.class;
		    case Short i -> short.class;
		    case Character c -> char.class;
		    default -> arg.getClass();
	    };
    }
}