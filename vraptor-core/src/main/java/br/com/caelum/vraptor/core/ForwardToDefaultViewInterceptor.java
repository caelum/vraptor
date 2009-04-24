package br.com.caelum.vraptor.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;
import br.com.caelum.vraptor.vraptor2.RequestResult;

/**
 * Intercepts the request and forwards to the default view if no view was
 * rendered so far.
 * 
 * @author Guilherme Silveira
 */
public class ForwardToDefaultViewInterceptor implements Interceptor {
    private final Result result;
    private final HttpServletRequest request;
    private final RequestResult methodResult;

    public ForwardToDefaultViewInterceptor(Result result, HttpServletRequest request, RequestResult methodResult) {
        this.result = result;
        this.request = request;
        this.methodResult = methodResult;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        if(result.used()) {
            return;
        }
        try {
            String accepts = request.getHeader("Accepts");
            result.use(DefaultPageResult.page()).forward(methodResult.getValue());
        } catch (ServletException e) {
            // TODO better?
            throw new InterceptionException(e);
        } catch (IOException e) {
            // TODO better?
            throw new InterceptionException(e);
        }
    }

}
