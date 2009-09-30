
package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Basic url to resource method translator.
 *
 * @author Guilherme Silveira
 * @author Leonardo Bessa
 */
@ApplicationScoped
public class DefaultResourceTranslator implements UrlToResourceTranslator {

    private final Logger logger = LoggerFactory.getLogger(DefaultResourceTranslator.class);
    static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    private final Router router;

    public DefaultResourceTranslator(Router router) {
        this.router = router;
    }

    public ResourceMethod translate(MutableRequest request) {
        String resourceName = getResourceName(request);
        if (logger.isDebugEnabled()) {
            logger.debug("trying to access " + resourceName);
        }
        ResourceMethod resource = router.parse(resourceName, HttpMethod.of(request), request);
        if (logger.isDebugEnabled()) {
            logger.debug("found resource " + resource);
        }
        return resource;
    }

    private String getResourceName(HttpServletRequest request) {
        if (request.getAttribute(INCLUDE_REQUEST_URI) != null) {
            return (String) request.getAttribute(INCLUDE_REQUEST_URI);
        }
        String uri = request.getRequestURI().replaceFirst("(?i);jsessionid=.*$", "");
        String contextName = request.getContextPath();
        uri = uri.replaceFirst(contextName, "");
        return uri;
    }

}
