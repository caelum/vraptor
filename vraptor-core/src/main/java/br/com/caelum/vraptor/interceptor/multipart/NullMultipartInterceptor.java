package br.com.caelum.vraptor.interceptor.multipart;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A null implementation of {@link MultipartInterceptor}. This class does nothing.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.3
 * @see CommonsUploadMultipartInterceptor
 */
public class NullMultipartInterceptor implements MultipartInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(NullMultipartInterceptor.class);
    
    private final HttpServletRequest request;
    
    public NullMultipartInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Only accept requests that contains multipart headers.
     */
    public boolean accepts(ResourceMethod method) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(Servlet3MultipartInterceptor.ACCEPT_MULTIPART);
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        logger.warn("You are willing to upload a file, but there is no commons-fileupload" +
            " or servlet3 handlers registered. Please add the commons-fileupload in your classpath" +
            " or use a Servlet 3 Container");
        
        stack.next(method, resourceInstance);
    }
}
