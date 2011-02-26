/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.DefaultExceptionMapper;
import br.com.caelum.vraptor.core.ExceptionMapper;
import br.com.caelum.vraptor.core.ExceptionRecorder;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.base.Throwables;

/**
 * Intercept all requests to handling uncaught exceptions.
 * <p>
 * This class is a part of Exception Handling Feature.
 * </p>
 *
 * @author Otávio Scherer Garcia
 * @see ExceptionRecorder
 * @see ExceptionRecorderParameter
 * @see ExceptionMapper
 * @see DefaultExceptionMapper
 * @since 3.2
 */
@Intercepts
public class ExceptionHandlerInterceptor
    implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerInterceptor.class);

    private final ExceptionMapper exceptions;
    private final Result result;
    private final HttpServletRequest request;

    public ExceptionHandlerInterceptor(ExceptionMapper exceptions, Result result, HttpServletRequest request) {
        this.exceptions = exceptions;
        this.result = result;
        this.request = request;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        try {
            stack.next(method, resourceInstance);
        } catch (InterceptionException e) {
            if (!(e.getCause() instanceof Exception) || !replay((Exception) e.getCause())) {
                throw e;
            }
        }
    }

    protected void reportException(Exception e) {
    	Throwable rootCause = Throwables.getRootCause(e);

        // add error attributes compliance with servlet spec
        result.include("javax.servlet.error.status_code", 500);
        result.include("javax.servlet.error.exception", rootCause);
        result.include("javax.servlet.error.exception_type", rootCause.getClass());
        result.include("javax.servlet.error.message", rootCause.getMessage());
        result.include("javax.servlet.error.request_uri", request.getRequestURI());
    }

    protected boolean replay(Exception e) {
        ExceptionRecorder<Result> exresult = exceptions.findByException(e);

        if (exresult == null) {
			return false;
		}

        reportException(e);

        logger.debug("handling exception {}", e.getClass());
        exresult.replay(result);

        return true;
    }

}
