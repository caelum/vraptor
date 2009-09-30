
package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * Interceptor which invokes vraptor2-like validation methods.
 * @author guilherme silveira
 *
 */
public class ValidatorInterceptor implements Interceptor {

    private final ParametersProvider provider;
    private final Result result;

    private static final Logger logger = LoggerFactory.getLogger(ValidatorInterceptor.class);
    private final ValidationErrors errors;
    private final OutjectionInterceptor outjection;
    private final Localization localization;
	private final MethodInfo info;

    public ValidatorInterceptor(ParametersProvider provider, Result result, ValidationErrors errors, OutjectionInterceptor outjection, Localization localization, MethodInfo info) {
        this.provider = provider;
        this.result = result;
        this.errors = errors;
        this.outjection = outjection;
        this.localization = localization;
		this.info = info;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (Info.isOldComponent(method.getResource())) {
            Class<?> type = method.getResource().getType();
            Method validationMethod = getValidationFor(method.getMethod(), type);
            if (validationMethod != null) {
                List<Message> convertionErrors = new ArrayList<Message>();
                ResourceBundle bundle = localization.getBundle();
                Object[] parameters = provider.getParametersFor(method, convertionErrors, bundle);
                Object[] validationParameters = new Object[parameters.length + 1];
                BasicValidationErrors newErrors = new BasicValidationErrors();
                validationParameters[0] = newErrors;
                System.arraycopy(parameters, 0, validationParameters, 1, parameters.length);
                try {
                    validationMethod.invoke(resourceInstance, validationParameters);
                } catch (IllegalArgumentException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (IllegalAccessException e) {
                    throw new InterceptionException("Unable to validate.", e);
                } catch (InvocationTargetException e) {
                    throw new InterceptionException("Unable to validate.", e.getCause());
                }
                for (Message msg : convertionErrors) {
                    errors.add(new FixedMessage(msg.getCategory(), msg.getMessage(), msg.getCategory()));
                }
                for (org.vraptor.i18n.ValidationMessage msg : newErrors) {
                    if (msg instanceof FixedMessage) {
                        errors.add(msg);
                    } else if (msg instanceof org.vraptor.i18n.Message) {
                        org.vraptor.i18n.Message m = (org.vraptor.i18n.Message) msg;
                        String content = localization.getMessage(m.getKey(), m.getParameters());
                        errors.add(new FixedMessage(msg.getPath(), content, msg.getCategory()));
                    } else {
                        throw new IllegalArgumentException("Unsupported validation message type: " + msg.getClass().getName());
                    }
                }
            }
            if (errors.size() != 0) {
                this.outjection.outject(resourceInstance, type);
                this.result.include("errors", errors);
                this.info.setResult("invalid");
                this.result.use(Results.page()).forward();
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
