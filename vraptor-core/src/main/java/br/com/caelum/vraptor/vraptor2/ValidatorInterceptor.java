package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ValidatorInterceptor implements Interceptor {

    private final ParametersProvider provider;
    private final PageResult result;

    private static final Logger logger = LoggerFactory.getLogger(ValidatorInterceptor.class);
    private final ValidationErrors errors;
    private final OutjectionInterceptor outjection;

    public ValidatorInterceptor(ParametersProvider provider, PageResult result, ValidationErrors errors, OutjectionInterceptor outjection) {
        this.provider = provider;
        this.result = result;
        this.errors = errors;
        this.outjection = outjection;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (Info.isOldComponent(method.getResource())) {
            Class<?> type = method.getResource().getType();
            Method validationMethod = getValidationFor(method.getMethod(), type);
            if (validationMethod != null) {
            	List<ValidationMessage> messages= new ArrayList<ValidationMessage>();
                Object[] parameters = provider.getParametersFor(method, messages, bundle);
                Object[] validationParameters = new Object[parameters.length + 1];
                validationParameters[0] = errors;
                for (int i = 0; i < parameters.length; i++) {
                    validationParameters[i + 1] = parameters[i];
                }
                try {
                    validationMethod.invoke(resourceInstance, validationParameters);
                } catch (IllegalArgumentException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (IllegalAccessException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (InvocationTargetException e) {
                    throw new InterceptionException("Unable to validate.", e.getCause());
                }
                for(ValidationMessage msg : messages) {
                	errors.add(new FixedMessage(msg.getCategory(), msg.getMessage(), msg.getCategory()));
                }
            }
            if (errors.size() != 0) {
                this.outjection.outject(resourceInstance, type);
                this.result.include("errors", errors);
                this.result.forward("invalid");
                return;
            }
        }
        stack.next(method, resourceInstance);
    }

    private <T> Method getValidationFor(Method method, Class<T> type) {
        String validationMethodName = "validate" + capitalize(method.getName());
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(validationMethodName)) {
                if (m.getParameterTypes().length != method.getParameterTypes().length + 1) {
                    logger.error("Validate method for " + method + " has a different number of args+1!");
                }
                return m;
            }
        }
        return null;
    }

    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
