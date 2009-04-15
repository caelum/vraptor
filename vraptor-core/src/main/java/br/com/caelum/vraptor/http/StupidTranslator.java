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

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Basic url to resource method translator.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class StupidTranslator implements UrlToResourceTranslator {

    private final ResourceRegistry registry;

    private static final Logger logger = LoggerFactory.getLogger(StupidTranslator.class);
    private static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    public StupidTranslator(ResourceRegistry registry) {
        this.registry = registry;
    }

    public ResourceMethod translate(HttpServletRequest request) {
        String resourceName = getURI(request);
        if (logger.isDebugEnabled()) {
            logger.debug("trying to access " + resourceName);
        }
        if (resourceName.length() > 1 && resourceName.indexOf('/', 1) != -1) {
            resourceName = resourceName.substring(resourceName.indexOf('/', 1));
        }
        String methodName = request.getMethod();
        ResourceMethod resource = registry.gimmeThis(resourceName, methodName);
        if (logger.isDebugEnabled()) {
            logger.debug("found resource " + resource);
        }
        return resource;
    }

    private String getURI(HttpServletRequest request) {
        if (request.getAttribute(INCLUDE_REQUEST_URI) != null) {
            return (String) request.getAttribute(INCLUDE_REQUEST_URI);
        }
        return request.getRequestURI();
    }

}
