/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.vraptor2;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.resource.DefaultResourceClass;


@ApplicationScoped
public class VRaptor2ComponentHandler implements StereotypeHandler {

	private static final Logger logger = LoggerFactory.getLogger(VRaptor2ComponentHandler.class);

	private final ComponentRegistry components;
	private final Router router;

	private final RoutesParser parser;

	public VRaptor2ComponentHandler(ComponentRegistry components, Router router, RoutesParser parser) {
		this.components = components;
		this.router = router;
		this.parser = parser;
	}

	public void handle(Class<?> type) {
		logger.debug("registering vraptor2 component {}", type);
		components.register(type, type);
		List<Route> rules = parser.rulesFor(new DefaultResourceClass(type));
		for (Route route : rules) {
			router.add(route);
		}
	}

	public Class<? extends Annotation> stereotype() {
		return Component.class;
	}

}
