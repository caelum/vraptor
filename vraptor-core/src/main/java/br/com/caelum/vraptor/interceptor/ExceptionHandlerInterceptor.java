package br.com.caelum.vraptor.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.ExceptionMapper;
import br.com.caelum.vraptor.core.ExceptionRecorder;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Intercept all requests to handling uncaught exceptions.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.2
 */
public class ExceptionHandlerInterceptor
    implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerInterceptor.class);

    private final ExceptionMapper exceptions;
    private final Result result;

    private ExceptionHandlerInterceptor(ExceptionMapper exceptions, Result result) {
        this.exceptions = exceptions;
        this.result = result;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        try {
            stack.next(method, resourceInstance);
        } catch (InterceptionException e) {
            reportException(e);
            replay(e);
        }
    }

    protected void reportException(Exception e) {
        // get the root cause
        while (e.getCause() != null && e.getCause() instanceof Exception) {
            e = (Exception) e.getCause();
        }

        // add error attributes compliance with servlets spec
        result.include("javax.servlet.error.status_code", 500);
        result.include("javax.servlet.error.exception", e);
    }

    private boolean replay(Exception e) {
        ExceptionRecorder<Result> exresult = exceptions.findByException(e);

        if (exresult != null) {
            logger.debug("handling exception {}", e.getClass());
            exresult.replay(result);

            return true;
        }

        if (e.getCause() != null) {
            return replay((Exception) e.getCause());
        }

        return false;
    }

}
