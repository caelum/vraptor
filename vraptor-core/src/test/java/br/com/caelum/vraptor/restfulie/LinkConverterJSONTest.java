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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;
import br.com.caelum.vraptor.restfulie.serialization.LinkConverterJSON;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * Ensure that JSON serialization, of Restful resources, contains resources links. And not restful resources remains untouched.
 * 
 * @author ac de souza
 */
public class LinkConverterJSONTest {

	private @Mock Restfulie restfulie;
	private @Mock RelationBuilder builder;
	private @Mock HypermediaResource resource;


	private XStream xstream;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.restfulie = mock(Restfulie.class);
		when(restfulie.newRelationBuilder()).thenReturn(builder);

		Configuration config = mock(Configuration.class);
		when(config.getApplicationPath()).thenReturn("http://www.caelum.com.br");

		xstream = new XStream(new JsonHierarchicalStreamDriver());
		ReflectionConverter base = new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider());

		xstream.registerConverter(new LinkConverterJSON(base, restfulie, config));
	}

	@Test
	public void shouldSerializeNoLinksIfThereIsNoTransition() {
		String json = xstream.toXML(resource);
		assertThat(json, not(containsString("links")));
	}

	@Test
	public void shouldSerializeOneLinkIfThereIsATransition() {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill));
		String json = xstream.toXML(resource);
		String expectedLinks = "\"links\": [\n    {\n      \"rel\": \"kill\",\n      \"href\": \"http://www.caelum.com.br/kill\"\n    }\n  ]";
		assertThat(json, containsString(expectedLinks));
	}
	
	@Test
	public void shouldSerializeAllLinksIfThereAreTransitions() {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		Relation ressurect = mock(Relation.class);
		when(ressurect.getName()).thenReturn("ressurect");
		when(ressurect.getUri()).thenReturn("/ressurect");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill, ressurect));
		String json = xstream.toXML(resource);
		String expectedLinks = "\"links\": [\n    {\n      \"rel\": \"kill\",\n      \"href\": \"http://www.caelum.com.br/kill\"\n    },\n    {\n      \"rel\": \"ressurect\",\n      \"href\": \"http://www.caelum.com.br/ressurect\"\n    }\n  ]";
		assertThat(json, containsString(expectedLinks));
	}
}
