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
/**
 *
 */
package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.view.LinkToHandler;

@ApplicationScoped
@org.springframework.stereotype.Component("stereotypeHandler")
public class ResourceHandler implements StereotypeHandler {
	private final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);
	private final Router router;
	private final RoutesParser parser;
	private final ServletContext context;

	public ResourceHandler(Router router, RoutesParser parser, ServletContext context) {
		this.router = router;
		this.parser = parser;
		this.context = context;
	}
	
	@PostConstruct
	public void configureLinkToHandler() {
		new LinkToHandler(context, router).start();
	}

	public void handle(Class<?> annotatedType) {
		logger.debug("Found resource: {}", annotatedType);
		List<Route> routes = parser.rulesFor(new DefaultResourceClass(annotatedType));
		for (Route route : routes) {
			router.add(route);
		}
		context.setAttribute(annotatedType.getSimpleName(), annotatedType);
	}

	public Class<? extends Annotation> stereotype() {
		return Resource.class;
	}
}