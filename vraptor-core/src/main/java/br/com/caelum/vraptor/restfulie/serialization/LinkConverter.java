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

package br.com.caelum.vraptor.restfulie.serialization;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.hypermedia.ConfigurableHypermediaResource;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Reads all transitions from your resource object and converts them into link
 * elements.<br>
 * The converter passed in the constructor will be used to marshall the rest of
 * the object.
 *
 * @author guilherme silveira
 * @author lucas cavalcanti
 */
public class LinkConverter implements Converter {

	private final Restfulie restfulie;
	private final Converter base;
	private final Configuration config;

	public LinkConverter(Converter base, Restfulie restfulie, Configuration config) {
		this.base = base;
		this.restfulie = restfulie;
		this.config = config;
	}

	public void marshal(Object root, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		if (root instanceof ConfigurableHypermediaResource) {
			context.convertAnother(((ConfigurableHypermediaResource) root).getModel());
		} else {
			base.marshal(root, writer, context);
		}

		HypermediaResource resource = (HypermediaResource) root;
		RelationBuilder builder = restfulie.newRelationBuilder();
		resource.configureRelations(builder);
		for (Relation t : builder.getRelations()) {
			writer.startNode("atom:link");
			writer.addAttribute("rel", t.getName());
			writer.addAttribute("href", config.getApplicationPath() + t.getUri());
			writer.addAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
			writer.endNode();
		}
	}

	private Object getRoot(Object root) {
		if (root instanceof ConfigurableHypermediaResource) {
			return ((ConfigurableHypermediaResource) root).getModel();
		}
		return root;
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		return base.unmarshal(arg0, arg1);
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return HypermediaResource.class.isAssignableFrom(type)
				&& base.canConvert(type);
	}

}
