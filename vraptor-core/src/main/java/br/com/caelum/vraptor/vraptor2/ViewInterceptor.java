package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewInterceptor implements Interceptor {

    private RequestResult reqResult;
    private final PageResult result;
    
    public ViewInterceptor(PageResult result, RequestResult reqResult) {
        this.result = result;
        this.reqResult = reqResult;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        try {
            this.result.forward(reqResult.getValue());
        } catch (ServletException e) {
            // TODO better
            throw new InterceptionException(e.getMessage(), e);
        }
    }

}
