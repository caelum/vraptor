package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.plugin.hibernate.HibernateLogicMethod;
import org.vraptor.plugin.hibernate.Validate;
import org.vraptor.plugin.hibernate.ValidatorLocator;
import org.vraptor.reflection.GettingException;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Support to vraptor2 hibernate validator plugin.<br>
 * Limited support to parameters only.
 * 
 * @author Guilherme Silveira
 */
public class HibernateValidatorPluginInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(HibernateValidatorPluginInterceptor.class);
    private final ValidationErrors errors;
    private final ParameterNameProvider provider;
    private final HttpServletRequest request;
    // sucks, cannot find a way to register without checking for hibernate
    // existence first...
    private final static ValidatorLocator locator = new ValidatorLocator();
    private final MethodParameters parameters;

    public HibernateValidatorPluginInterceptor(ValidationErrors errors, ParameterNameProvider provider,
            HttpServletRequest request, MethodParameters parameters) {
        this.errors = errors;
        this.provider = provider;
        this.request = request;
        this.parameters = parameters;
    }

    public boolean accepts(ResourceMethod method) {
        return method.getMethod().isAnnotationPresent(Validate.class);
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        Validate validate = method.getMethod().getAnnotation(Validate.class);
        if (validate != null) {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "org.hibernate.validator.resources.DefaultValidatorMessages", request.getLocale());
            String[] names = provider.parameterNamesFor(method.getMethod());
            for (String path : validate.params()) {
                try {
                    Object[] paramValues = parameters.getValues();
                    Object object = paramFor(names, path, paramValues);
                    HibernateLogicMethod.validateParam(locator, request, bundle, errors, object, path);
                } catch (GettingException e) {
                    throw new InterceptionException(
                            "Unable to validate objects due to an exception during validation.", e);
                }
            }
        }
        stack.next(method, resourceInstance);
    }

    private Object paramFor(String[] names, String path, Object[] values) throws InterceptionException {
        String param = path;
        if (param.indexOf(".") != -1) {
            param = param.substring(param.indexOf("."));
        }
        param = Info.capitalize(param);
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(param)) {
                return values[i];
            }
        }
        throw new InterceptionException("Unable to find param for hibernate validator: '" + path + "'");
    }

}
