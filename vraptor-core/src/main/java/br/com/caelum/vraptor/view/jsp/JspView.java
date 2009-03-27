package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.PathResolver;

/**
 * A jsp view which can be customized by providing your own PathConstructor.
 * 
 * @author Guilherme Silveira
 */
public class JspView implements View {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ResourceMethod method;
    private final PathResolver resolver;

    public JspView(HttpServletRequest req, HttpServletResponse res, ResourceMethod method) {
        this(req,res,method, new DefaultPathResolver());
    }

    public JspView(HttpServletRequest req, HttpServletResponse res, ResourceMethod method,
            PathResolver resolver) {
        this.request = req;
        this.response = res;
        this.method = method;
        this.resolver = resolver;
    }

    public static Class<JspView> jsp() {
        return JspView.class;
    }

    public void forward(String result) throws ServletException, IOException {
        request.getRequestDispatcher(resolver.pathFor(method, result)).forward(request, response);
    }

    public void include(String result) throws ServletException, IOException {
        request.getRequestDispatcher(resolver.pathFor(method, result)).include(request, response);
    }

}
