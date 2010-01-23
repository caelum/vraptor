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
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.Relation;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.hypermedia.LinkConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

public class LinkConverterTest {
	
	public static class Client implements HypermediaResource{
		
		private transient final Relation[] list;

		public Client(Relation ... t) {
			this.list = t;
		}

		public List<Relation> getRelations(Restfulie control) {
			return Arrays.asList(list);
		}
		
	}
	private XStream xstream;
	
	@Before
	public void setup() {
		this.xstream = new XStream();
		Configuration config = mock(Configuration.class);
		when(config.getApplicationPath()).thenReturn("http://www.caelum.com.br");
		xstream.registerConverter(new LinkConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()), null, config));
	}
	
	@Test
	public void shouldSerializeNoLinksIfThereIsNoTransition() {
		String xml = xstream.toXML(new Client());
		assertThat(xml, not(containsString("atom:link")));
	}
	
	@Test
	public void shouldSerializeOneLinkIfThereIsATransition() {
		Relation t = mock(Relation.class);
		when(t.getName()).thenReturn("kill");
		when(t.getUri()).thenReturn("/kill");
		
		String xml = xstream.toXML(new Client(t));
		assertThat(xml, containsString("<atom:link rel=\"kill\" href=\"http://www.caelum.com.br/kill\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
	}
	
	@Test
	public void shouldSerializeAllLinksIfThereAreTransitions() {
		Relation t = mock(Relation.class);
		when(t.getName()).thenReturn("kill");
		when(t.getUri()).thenReturn("/kill");
		
		Relation t2 = mock(Relation.class);
		when(t2.getName()).thenReturn("ressurect");
		when(t2.getUri()).thenReturn("/ressurect");
		
		String xml = xstream.toXML(new Client(t,t2));
		assertThat(xml, containsString("<atom:link rel=\"kill\" href=\"http://www.caelum.com.br/kill\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
		assertThat(xml, containsString("<atom:link rel=\"ressurect\" href=\"http://www.caelum.com.br/ressurect\" xmlns:atom=\"http://www.w3.org/2005/Atom\"/>"));
	}
	
}
