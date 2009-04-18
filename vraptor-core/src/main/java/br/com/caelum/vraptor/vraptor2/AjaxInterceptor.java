package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.vraptor.remote.json.JSONSerializer;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

/**
 * VRaptor2 based ajax interceptor.
 * 
 * @author Guilherme Silveira
 */
public class AjaxInterceptor implements Interceptor {

    private static final String UTF8 = "UTF-8";

    private final Outjecter outjecter;

    private final ComponentInfoProvider info;

    private final HttpServletResponse response;

    public AjaxInterceptor(Outjecter outjecter, HttpServletResponse response, ComponentInfoProvider info) {
        this.outjecter = outjecter;
        this.response = response;
        this.info = info;

    }

    public boolean accepts(ResourceMethod method) {
        // TODO this is not invoked as automatically loaded thorugh
        // RequestExecution
        // it should be included on the ExtractorList so would not be invoked?
        return info.isAjax();
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        if (info.isAjax()) {
            JsonOutjecter outjecter = (JsonOutjecter) this.outjecter;
            CharSequence output = new JSONSerializer().serialize(outjecter.contents());
            response.setCharacterEncoding(UTF8);
            response.setContentType("application/json");

            PrintWriter writer = response.getWriter();
            writer.append(output);
            writer.flush();
            writer.close();
        } else {
            stack.next(method, resourceInstance);
        }
    }

}
