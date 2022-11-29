package no.ding.pk.utils;

import java.lang.reflect.Field;

public class MergeUtils {
    public static <T> T mergeObjects(T first, T second) {
        Class<?> clas = first.getClass();
        Field[] fields = clas.getDeclaredFields();
        Object result = null;

        try {
            result = clas.getDeclaredConstructor(null).newInstance();
            for(Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(first);
                Object value2 = field.get(second);

                Object value = null;
                if(value1 == null && value2 != null) {
                    value = value2;
                } else if(value1 != null && !value1.equals(value2)) {
                    value = value2;
                } else {
                    value = value1;
                }
                field.set(result, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T) result;
    }
}
