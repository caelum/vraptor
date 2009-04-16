package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Paranamer based parameter name provider provides parameter names based on
 * their local variable name during compile time. Information is retrieved using
 * paranamer's mechanism.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider{

    private static final String[] EMPTY_ARRAY = new String[0];
    private final ParameterNameProvider delegate = new DefaultParameterNameProvider();
    private final Paranamer info = new CachingParanamer(new BytecodeReadingParanamer());
    
    private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);

    public String[] parameterNamesFor(Method method) {
        if (method.getParameterTypes().length == 0) {
            return EMPTY_ARRAY;
        }
        try {
            String[] parameterNames = info.lookupParameterNames(method);
            if (logger.isDebugEnabled()) {
                logger.debug("Found parameter names with paranamer for " + method + " as " + Arrays.toString(parameterNames));
            }
            return parameterNames;
        } catch (ParameterNamesNotFoundException e) {
            return delegate.parameterNamesFor(method);
        }
    }

    public String nameFor(Type type) {
        if (type instanceof ParameterizedType) {
            return extractName((ParameterizedType) type);
        }
        return extractName((Class<?>) type);
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
