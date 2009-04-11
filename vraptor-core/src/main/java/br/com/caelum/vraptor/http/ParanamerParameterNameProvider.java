package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.picocontainer.paranamer.BytecodeReadingParanamer;
import org.picocontainer.paranamer.CachingParanamer;
import org.picocontainer.paranamer.Paranamer;
import org.vraptor.component.DefaultParameterInfoProvider;
import org.vraptor.component.MethodParameter;
import org.vraptor.component.ParameterInfoProvider;
import org.vraptor.component.ParanamerParameterInfoProvider;

/**
 * Paranamer based parameter name provider provides parameter names based on
 * their local variable name during compile time. Information is retrieved using
 * paranamer's mechanism.
 * 
 * @author Guilherme Silveira
 */
public class ParanamerParameterNameProvider {

    private static final List<MethodParameter> EMPTY_LIST = new ArrayList<MethodParameter>();
    private final ParameterInfoProvider delegate = new DefaultParameterInfoProvider();
    private final Paranamer infoProvider = new CachingParanamer(new BytecodeReadingParanamer());
    private static final Logger LOGGER = Logger.getLogger(ParanamerParameterInfoProvider.class);

    public List<MethodParameter> provideFor(Method method) {
        if (method.getParameterTypes().length == 0) {
            return EMPTY_LIST;
        }
        Class<?> declaringType = method.getDeclaringClass();
        List<MethodParameter> original = delegate.provideFor(method);
        if (infoProvider.areParameterNamesAvailable(declaringType, method.getName()) != Paranamer.PARAMETER_NAMES_FOUND) {
            return original;
        }
        String[] parameterNames = infoProvider.lookupParameterNames(method);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found parameter names with paranamer for " + method + " as " + parameterNames);
        }
        List<MethodParameter> modified = new ArrayList<MethodParameter>();
        int i = 0;
        for (MethodParameter p : original) {
            modified.add(new MethodParameter(p.getType(), p.getGenericType(), p.getPosition(), parameterNames[i++]));
        }
        return modified;
    }

    public String nameFor(Type type) {
        if (type instanceof ParameterizedType) {
            return extractName((ParameterizedType) type);
        }
        return extractName((Class) type);
    }

    public String extractName(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractName((Class<?>) raw) + "Of";
        Type[] types = type.getActualTypeArguments();
        for (Type t : types) {
            name += extractName((Class<?>) t);
        }
        return name;
    }

    public String extractName(Class<?> type) {
        if (type.isArray()) {
            return type.getComponentType().getSimpleName();
        }
        return type.getSimpleName();
    }

}
