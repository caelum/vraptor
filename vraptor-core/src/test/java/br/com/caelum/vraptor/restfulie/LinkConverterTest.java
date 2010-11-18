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
import static org.mockito.Mockito.verify;
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
import br.com.caelum.vraptor.restfulie.serialization.LinkConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

public class LinkConverterTest {

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

		xstream = new XStream();
		ReflectionConverter base = new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider());

		xstream.registerConverter(new LinkConverter(base, restfulie, config));
	}

	@Test
	public void shouldSetupRelations() {
		xstream.toXML(resource);
		verify(resource).configureRelations(builder);
	}

	@Test
	public void shouldSerializeNoLinksIfThereIsNoTransition() {
		String xml = xstream.toXML(resource);
		assertThat(xml, not(containsString("atom:link")));
	}

	@Test
	public void shouldSerializeOneLinkIfThereIsATransition() {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill));
		String xml = xstream.toXML(resource);
		assertThat(xml, containsString("<atom:link rel=\"kill\" href=\"http://www.caelum.com.br/kill\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
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
		String xml = xstream.toXML(resource);
		assertThat(xml, containsString("<atom:link rel=\"kill\" href=\"http://www.caelum.com.br/kill\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
		assertThat(xml, containsString("<atom:link rel=\"ressurect\" href=\"http://www.caelum.com.br/ressurect\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
	}

}
