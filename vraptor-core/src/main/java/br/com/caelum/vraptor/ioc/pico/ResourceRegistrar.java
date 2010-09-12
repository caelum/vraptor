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

package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ResourceHandler;

/**
 * Prepares special components annotated with @Resource to be reachable through web requests;
 * i.e. adds them to the Router.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class ResourceRegistrar implements Registrar {
    private final Logger logger = LoggerFactory.getLogger(ResourceRegistrar.class);

	private ResourceHandler resourceHandler;

    public ResourceRegistrar(Router router, RoutesParser parser) {
        resourceHandler = new ResourceHandler(router, parser);
    }

    public void registerFrom(Scanner scanner) {
        logger.info("Registering all resources annotated with @Resource");
        Collection<Class<?>> resourceTypes = scanner.getTypesWithAnnotation(Resource.class);
        for (Class<?> resourceType : resourceTypes) {
        	resourceHandler.handle(resourceType);
        }
    }
}
