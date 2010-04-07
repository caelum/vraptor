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

package br.com.caelum.vraptor.restfulie.headers;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.restfulie.RestHeadersHandler;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.resource.Cacheable;
import br.com.caelum.vraptor.restfulie.resource.RestfulEntity;

@Component
public class DefaultRestHeadersHandler implements RestHeadersHandler {

	private final HttpServletResponse response;
	private final RestDefaults defaults;

	public DefaultRestHeadersHandler(HttpServletResponse response, RestDefaults defaults) {
		this.defaults = defaults;
		this.response = response;
	}

	public void handle(HypermediaResource resource) {
		// TODO implement link headers
		if(Cacheable.class.isAssignableFrom(resource.getClass())) {
			Cacheable cache = (Cacheable) resource;
			response.addHeader("Cache-control","max-age=" + cache.getMaximumAge());
		}
		if(RestfulEntity.class.isInstance(resource)) {
			RestfulEntity entity = (RestfulEntity) resource;
			restfulHeadersFor(entity.getEtag(), entity.getLastModified());
		} else {
			restfulHeadersFor(defaults.getEtagFor(resource), defaults.getLastModifiedFor(resource));
		}

//		TagCoisa t = tagInfoFor(resource);
//		resource.getRelations(control)
//		Relation rel;
//		rel.getName()
//		rel.getUri()
//		addHeader("Link", "rel=name;uri=uri")
//		precisa limpar depois

	}

	private void restfulHeadersFor(String etag, Calendar lastModified) {
		response.addHeader("ETag", etag);
		if(lastModified!=null) {
			response.setDateHeader("Last-modified", lastModified.getTimeInMillis());
		}
	}


}
