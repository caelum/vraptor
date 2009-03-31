package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Interceptor that executes the logic method.
 * 
 * @author Guilherme Silveira
 */
public class ExecuteMethodInterceptor implements Interceptor {

    private final ParametersProvider provider;

    public ExecuteMethodInterceptor(ParametersProvider provider) {
        this.provider = provider;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance)
            throws IOException, InterceptionException {
        try {
            Method reflectionMethod = method.getMethod();
            Object[] parameters = provider.getParametersFor(method);
            reflectionMethod.invoke(resourceInstance, parameters);
        } catch (IllegalArgumentException e) {
            throw new InterceptionException(e);
        } catch (IllegalAccessException e) {
            throw new InterceptionException(e);
        } catch (InvocationTargetException e) {
            throw new InterceptionException(e.getCause());
        }
    }

}
