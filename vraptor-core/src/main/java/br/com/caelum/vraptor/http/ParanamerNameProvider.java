package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Paranamer based parameter name provider provides parameter names based on
 * their local variable name during compile time. Information is retrieved using
 * paranamer's mechanism.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider {

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

}
