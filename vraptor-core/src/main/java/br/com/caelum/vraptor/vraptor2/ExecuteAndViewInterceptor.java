package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ExecuteAndViewInterceptor implements Interceptor {

    private final ParametersProvider provider;
    private final PageResult result;

    public ExecuteAndViewInterceptor(ParametersProvider provider, PageResult result) {
        this.provider = provider;
        this.result = result;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance)
            throws IOException, InterceptionException {
        try {
            Method reflectionMethod = method.getMethod();
            Object[] parameters = provider.getParametersFor(method);
            Object result = reflectionMethod.invoke(resourceInstance, parameters);
            if (Info.isOldComponent(method.getResource())) {
                if (result == null) {
                    result = "ok";
                }
                this.result.forward((String) result);
            }
        } catch (IllegalArgumentException e) {
            throw new InterceptionException(e);
        } catch (IllegalAccessException e) {
            throw new InterceptionException(e);
        } catch (InvocationTargetException e) {
            throw new InterceptionException(e.getCause());
        } catch (ServletException e) {
            throw new InterceptionException(e);
        }
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
