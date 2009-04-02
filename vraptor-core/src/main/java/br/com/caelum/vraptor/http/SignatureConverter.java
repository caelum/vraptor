package br.com.caelum.vraptor.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SignatureConverter {

    private static Map<Class<?>, String> WRAPPERS = new HashMap<Class<?>, String>();
    static {
        WRAPPERS.put(boolean.class, "Z");
        WRAPPERS.put(int.class, "I");
        WRAPPERS.put(short.class, "S");
        WRAPPERS.put(long.class, "J");
        WRAPPERS.put(double.class, "D");
        WRAPPERS.put(float.class, "F");
        WRAPPERS.put(byte.class, "B");
        WRAPPERS.put(char.class, "C");
    }

    public String wrapperFor(Class<?> type) {
        return WRAPPERS.get(type);
    }

    public String extractTypeDefinition(Class type) {
        if(type.isArray()) {
            return "[" + extractTypeDefinition(type.getComponentType());
        }
        if (type.isPrimitive()) {
            return WRAPPERS.get(type);
        }
        return 'L' + type.getName().replace('.', '/') + ';';
    }


    String extractTypeDefinition(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractTypeDefinition((Class) raw);
        name = name.substring(0,name.length()-1) + "<";
        Type[] types = type.getActualTypeArguments();
        for(Type t : types) {
            name += extractTypeDefinition((Class) t);
        }
        return name + ">;";
    }


}
