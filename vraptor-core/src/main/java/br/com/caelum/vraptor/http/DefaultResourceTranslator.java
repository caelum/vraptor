/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
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
    private static final String METHOD_PARAMETER = "_method";
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
        String methodName = request.getParameter(METHOD_PARAMETER);
        if (methodName == null) {
            methodName = request.getMethod();
        }
        HttpMethod requestMethod = HttpMethod.valueOf(methodName.toUpperCase());
        ResourceMethod resource = router.parse(resourceName, requestMethod, request);
        if (logger.isDebugEnabled()) {
            logger.debug("found resource " + resource);
        }
        return resource;
    }

    private String getResourceName(HttpServletRequest request) {
        if (request.getAttribute(INCLUDE_REQUEST_URI) != null) {
            return (String) request.getAttribute(INCLUDE_REQUEST_URI);
        }
        String uri = request.getRequestURI();
        String contextName = request.getContextPath();
        uri = uri.replaceFirst(contextName, "");
        return uri;
    }

}
