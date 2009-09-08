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
package br.com.caelum.vraptor.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.MissingConfigurationException;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

/**
 * VRaptors servlet context init parameter configuration reader.
 *
 * @author Guilherme Silveira
 */
public class BasicConfiguration {

	/**
	 * context parameter that represents the class of IoC provider
	 */
    public static final String CONTAINER_PROVIDER = "br.com.caelum.vraptor.provider";

    /**
     * context parameter that represents the base package(s) of your application
     */
    public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.packages";

    private final ServletContext servletContext;

    public BasicConfiguration(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ContainerProvider getProvider() throws ServletException {
        String provider = servletContext.getInitParameter(CONTAINER_PROVIDER);
        if (provider == null) {
            provider = SpringProvider.class.getName();
        }
        try {
            return (ContainerProvider) Class.forName(provider).getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw new ServletException(e.getCause());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public String getBasePackages() {
    	String packages = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
    	if (packages == null) {
			throw new MissingConfigurationException(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME + " context-param not found in web.xml. " +
					"Set this parameter with your base package");
		}
    	return packages;
    }

}
