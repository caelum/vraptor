package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.core.WebRequest;
import org.vraptor.http.VRaptorServletRequest;
import org.vraptor.http.VRaptorServletResponse;
import org.vraptor.introspector.ExpressionEvaluationException;
import org.vraptor.introspector.ExpressionEvaluator;
import org.vraptor.scope.DefaultLogicRequest;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewsPropertiesPageResult implements PageResult {

    private final Config config;
    private final HttpServletRequest request;
    private final PathResolver resolver;
    private final ResourceMethod method;
    private final HttpServletResponse response;
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final DefaultLogicRequest logic;

    private static final Logger logger = LoggerFactory.getLogger(ViewsPropertiesPageResult.class);

    public ViewsPropertiesPageResult(Config config, HttpServletRequest request, PathResolver resolver,
            ResourceMethod method, HttpServletResponse response, ServletContext context) {
        this.config = config;
        this.request = request;
        this.resolver = resolver;
        this.method = method;
        this.response = response;
        logic = new DefaultLogicRequest(null, new WebRequest(new VRaptorServletRequest(request, null),
                new VRaptorServletResponse(response), context), null);
    }

    public void forward(String result) throws ServletException, IOException {
        Resource resource = method.getResource();
        if(!Info.isOldComponent(resource)) {
            request.getRequestDispatcher(resolver.pathFor(method, result)).forward(request, response);
            return;
        }
        String key = Info.getComponentName(resource.getType()) + "."
                + Info.getLogicName(method.getMethod()) + "." + result;

        String path = config.getForwardFor(key);

        if (path == null) {
            request.getRequestDispatcher(resolver.pathFor(method, result)).forward(request, response);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("overriden view found for " + key + " : " + path);
            }
            try {
                result = evaluator.parseExpression(path, logic);
            } catch (ExpressionEvaluationException e) {
                throw new IOException("Unable to redirect while evaluating expression '" + path + "'.", e);
            }
            if (path.startsWith("redirect:")) {
                response.sendRedirect(path.substring(9));
            } else {
                request.getRequestDispatcher(path).forward(request, response);
            }
        }
    }

    public void include(String result) throws ServletException, IOException {
        request.getRequestDispatcher(resolver.pathFor(method, result)).include(request, response);
    }

    public void include(String key, Object value) {
        request.setAttribute(key, value);
    }

}
