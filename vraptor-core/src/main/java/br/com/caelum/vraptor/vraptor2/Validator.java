package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;

import org.vraptor.validator.BasicValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class Validator implements Interceptor {

    private final ParametersProvider provider;
    private final PageResult result;

    public Validator(ParametersProvider provider, PageResult result) {
        this.provider = provider;
        this.result = result;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        if (Info.isOldComponent(method.getResource())) {
            Object[] parameters = provider.getParametersFor(method);
            Object[] validationParameters = new Object[parameters.length + 1];
            BasicValidationErrors errors = new BasicValidationErrors();
            validationParameters[0] = errors;
            for (int i = 0; i < parameters.length; i++) {
                validationParameters[i + 1] = parameters[i];
            }
            Method validationMethod = getValidationFor(method.getMethod(), method.getResource().getType());
            if (validationMethod != null) {
                try {
                    validationMethod.invoke(resourceInstance, validationParameters);
                } catch (IllegalArgumentException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (IllegalAccessException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (InvocationTargetException e) {
                    throw new InterceptionException("Unable to validate.", e.getCause());
                }
                if (errors.size() != 0) {
                    try {
                        this.result.include("errors", errors);
                        this.result.forward("invalid");
                    } catch (ServletException e) {
                        throw new InterceptionException("Unable to forward", e.getCause());
                    }
                    return;
                }
            }
        }
        stack.next(method, resourceInstance);
    }

    private <T> Method getValidationFor(Method method, Class<T> type) {
        String validationMethodName = "validate" + capitalize(method.getName());
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(validationMethodName)) {
                return m;
            }
        }
        return null;
    }

    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
