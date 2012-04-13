/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.config;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.guice.GuiceProvider;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import br.com.caelum.vraptor.ioc.spring.MissingConfigurationException;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;

/**
 * VRaptors servlet context init parameter configuration reader.
 *
 * @author Guilherme Silveira
 */
public class BasicConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(BasicConfiguration.class);

	/**
	 * context parameter that represents the class of IoC provider
	 */
	public static final String CONTAINER_PROVIDER = "br.com.caelum.vraptor.provider";

	/**
	 * context parameter that represents application character encoding
	 */
	public static final String ENCODING = "br.com.caelum.vraptor.encoding";

	/**
	 * context parameter that represents the base package(s) of your application
	 */
	public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.packages";

	/**
	 * Disables/enables classpath scanning
	 */
	public static final String SCANNING_PARAM = "br.com.caelum.vraptor.scanning";

	private final ServletContext servletContext;

	public BasicConfiguration(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ContainerProvider getProvider() throws ServletException {
		Class<? extends ContainerProvider> providerType = getProviderType();
		logger.info("Using {} as Container Provider", providerType);
		try {
			return providerType.getDeclaredConstructor().newInstance();
		} catch (InvocationTargetException e) {
			throw new ServletException(e.getCause());
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private Class<? extends ContainerProvider> getProviderType() {
		String provider = servletContext.getInitParameter(CONTAINER_PROVIDER);
		if (provider != null) {
			try {
				return (Class<? extends ContainerProvider>) Class.forName(provider);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("You must configure a class that exists on the "
						+ CONTAINER_PROVIDER + " context param.", e);
			}
		}
		if (classExists("org.springframework.context.ApplicationContext")) {
			return SpringProvider.class;
		}
		if (classExists("com.google.inject.Guice")) {
			return GuiceProvider.class;
		}

		if (classExists("org.picocontainer.PicoContainer")) {
			return PicoProvider.class;
		}
		throw new IllegalArgumentException("You don't have any DI container jars on your classpath. " +
				"You can find them on vraptor-3.x.x.zip, so you must put one of the " +
				"lib/containers/<container> jars on your classpath, where <container> is your preferred container.");
	}

	private boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public boolean hasBasePackages() {
		return servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME) != null;
	}

	public String[] getBasePackages() {
		String packages = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
		if (packages == null) {
			throw new MissingConfigurationException(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME
					+ " context-param not found in web.xml. Set this parameter with your base package");
		}
		return packages.trim().split(",\\s*");
	}

	public String getEncoding() {
		return servletContext.getInitParameter(ENCODING);
	}

	public String getWebinfClassesDirectory() {
		return servletContext.getRealPath("/WEB-INF/classes/");
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public boolean isClasspathScanningEnabled() {
		String scanningParam = servletContext.getInitParameter(SCANNING_PARAM);
		logger.info("{} = {}", SCANNING_PARAM, servletContext.getInitParameter(SCANNING_PARAM));
		return scanningParam == null || !scanningParam.trim().equals("disabled");
	}

}
