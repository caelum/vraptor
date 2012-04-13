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
package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default implementation for SpringLocator.
 * It tries to use spring default location to create the ApplicationContext
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class DefaultSpringLocator implements SpringLocator {

	private static final Logger logger = LoggerFactory.getLogger(DefaultSpringLocator.class);

	public ConfigurableWebApplicationContext getApplicationContext(ServletContext servletContext) {
		ConfigurableWebApplicationContext context = (ConfigurableWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(servletContext);
		if (context != null) {
			logger.info("Using a web application context: {}", context);
			return context;
		}
		if (DefaultSpringLocator.class.getResource("/applicationContext.xml") != null) {
			logger.info("Using an XmlWebApplicationContext, searching for applicationContext.xml");
			XmlWebApplicationContext ctx = new XmlWebApplicationContext();
			ctx.setConfigLocation("classpath:applicationContext.xml");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
			return ctx;
		}
		logger.info("No application context found");
		ConfigurableWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.setId("VRaptor");
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
		return ctx;
	}

}
