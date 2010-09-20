package br.com.caelum.vraptor.interceptor;

import java.util.Map.Entry;

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
            ExceptionRecorder<Result> o = findResultByException(e);
            if (o == null) {
                // there are no mapped exceptions
                throw e;
            }

            reportException(e);
            replay(e);
        }
    }

    protected ExceptionRecorder<Result> findResultByException(Exception exception) {
        logger.debug("find for exception {}", exception.getClass());

        for (Entry<Class<? extends Exception>, ExceptionRecorder<Result>> entry : exceptions.entries()) {
            if (entry.getKey().isInstance(exception)) { // with children exceptions
                logger.debug("found exception mapping: {} -> {}", entry.getKey(), entry.getValue());

                return entry.getValue();
            }
        }

        return null;
    }

    protected void reportException(Exception e) {
        while (e.getCause() != null && e.getCause() instanceof Exception)
            e = (Exception) e.getCause();

        // add error attributes compliance with servlets spec
        result.include("javax.servlet.error.status_code", 500);
        result.include("javax.servlet.error.exception", e);
    }

    private boolean replay(Exception exception) {
        ExceptionRecorder<Result> exresult = findResultByException(exception);

        if (exresult != null) {
            exresult.replay(result);
            return true;
        }

        if (exception.getCause() != null) {
            return replay((Exception) exception.getCause());
        }

        return false;
    }

}
