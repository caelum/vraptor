package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationError;

public class ExecuteAndViewInterceptor implements Interceptor {

    private final RequestResult result;
    private final MethodParameters parameters;

    public ExecuteAndViewInterceptor(RequestResult result, MethodParameters parameters) {
        this.result = result;
        this.parameters = parameters;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws IOException, InterceptionException {
        try {
            Method reflectionMethod = method.getMethod();
            Object[] parameters = this.parameters.getValues();
            Object result = reflectionMethod.invoke(resourceInstance, parameters);
            if (Info.isOldComponent(method.getResource())) {
                if (result == null) {
                    this.result.setValue("ok");
                } else {
                    this.result.setValue((String) result);
                }
            }
            stack.next(method, resourceInstance);
        } catch (ValidationError e) {
            // fine... already parsed
        } catch (IllegalArgumentException e) {
            throw new InterceptionException(e);
        } catch (IllegalAccessException e) {
            throw new InterceptionException(e);
        } catch (InvocationTargetException e) {
            throw new InterceptionException(e.getCause());
        }
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
