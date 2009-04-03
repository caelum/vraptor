package br.com.caelum.vraptor.converter;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import ognl.TypeConverter;
import br.com.caelum.vraptor.core.Converters;

public class OgnlBasedConverterController implements TypeConverter {
    
    private final Converters converters;

    public OgnlBasedConverterController(Converters converters) {
        this.converters = converters;
    }

    @SuppressWarnings("unchecked")
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        if(!(member instanceof Method)) {
            throw new IllegalArgumentException("Vraptor can only navigate through getter/setter methods");
        }
        Method method = (Method) member;
        Type[] parameterTypes = method.getGenericParameterTypes();
        if(parameterTypes.length!=1) {
            throw new IllegalArgumentException("Vraptor can only navigate through setters with one parameter");
        }
        Type parameterType = parameterTypes[0];
        Class type;
        if(parameterType instanceof ParameterizedType) {
            type = (Class) ((ParameterizedType) parameterType).getRawType();
        } else {
            type = (Class) parameterType;
        }
        return converters.to(type).convert((String) value);
    }

}
