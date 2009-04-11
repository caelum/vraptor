package br.com.caelum.vraptor.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultParameterNameProvider implements ParameterNameProvider{

    public String extractName(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractName((Class<?>) raw) + "Of";
        Type[] types = type.getActualTypeArguments();
        for(Type t : types) {
            name += extractName((Class<?>) t);
        }
        return name;
    }

    public String extractName(Class<?> type) {
        if(type.isArray()) {
            return type.getComponentType().getSimpleName();
        }
        return type.getSimpleName();
    }

    public String nameFor(Type type) {
        if(type instanceof ParameterizedType) {
            return extractName((ParameterizedType) type);
        }
        return extractName((Class) type);
    }

}
