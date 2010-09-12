/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
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

package br.com.caelum.vraptor.restfulie;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.restfulie.hypermedia.ConfigurableHypermediaResource;
import br.com.caelum.vraptor.restfulie.hypermedia.DefaultConfigurableHypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.DefaultRelationBuilder;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

/**
 * Default implementation for {@link Restfulie}
 *
 * @author Lucas Cavalcanti
 * @since 3.2.0
 */
@Component
@ApplicationScoped
public class DefaultRestfulie implements Restfulie {

	private final Proxifier proxifier;
	private final Router router;

	public DefaultRestfulie(Proxifier proxifier, Router router) {
		this.proxifier = proxifier;
		this.router = router;
	}

	public RelationBuilder newRelationBuilder() {
		return new DefaultRelationBuilder(router, proxifier);
	}

	public ConfigurableHypermediaResource enhance(Object object) {
		return new DefaultConfigurableHypermediaResource(newRelationBuilder(), object);
	}

}
