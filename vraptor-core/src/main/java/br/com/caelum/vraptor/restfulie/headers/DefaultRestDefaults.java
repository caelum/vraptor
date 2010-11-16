/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.restfulie.headers;

import java.util.Calendar;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;

/**
 * Default implementation for REST defaults.
 * Uses resource's hash code to generate ETag and doesn't provide a lastModified value.
 * @author Lucas Cavalcanti
 * @since 3.1.2
 */
@Component
@ApplicationScoped
public class DefaultRestDefaults implements RestDefaults {

	public String getEtagFor(HypermediaResource resource) {
		return resource.hashCode() + "";
	}

	public Calendar getLastModifiedFor(HypermediaResource resource) {
		return null;
	}

}
