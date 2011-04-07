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

package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.com.caelum.vraptor.http.route.Evaluator;
import org.junit.Before;
import org.junit.Test;

public class JavaEvaluatorTest {

	private Evaluator evaluator;

	@Before
	public void setup() {
		this.evaluator = new JavaEvaluator();
	}

	class Client {
		private Long id;
		private Client child;
		private List<String> emails;
		private Set<Integer> favoriteNumbers;
		private String[] favoriteColors;
		private boolean ugly;

		public Client(Long id) {
			this.id = id;
		}

		public Client getChild() {
			return child;
		}

		public Long getId() {
			return id;
		}

		public Set<Integer> getFavoriteNumbers() {
			return favoriteNumbers;
		}

		public List<String> getEmails() {
			return emails;
		}

		public String[] getFavoriteColors() {
			return favoriteColors;
		}

		public boolean isUgly() {
			return ugly;
		}
	}

	class VipClient extends Client {
		public VipClient(Long id) {
			super(id);
		}
	}

	@Test
	public void shouldInvokeAGetter() {
		assertThat((Long) evaluator.get(client(1L), "client.id"), is(equalTo(1L)));
	}

	@Test
	public void shouldInvokeAIs() {
		Client c = client(1L);
		c.ugly=true;
		assertThat((Boolean) evaluator.get(c, "client.ugly"), is(equalTo(true)));
	}

	@Test
	public void shouldAccessArray() {
		Client c = client(1L);
		c.favoriteColors = new String[] {"blue", "red"};
		assertThat((String) evaluator.get(c, "client.favoriteColors[1]"), is(equalTo("red")));
	}

	@Test
	public void shouldAccessList() {
		Client c = client(1L);
		c.emails = Arrays.asList("blue", "red");
		assertThat((String) evaluator.get(c, "client.emails[1]"), is(equalTo("red")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAccessCollection() {
		Client c = client(1L);
		c.favoriteNumbers = new TreeSet(Arrays.asList("blue", "red"));
		assertThat((String) evaluator.get(c, "client.favoriteNumbers[1]"), is(equalTo("red")));
	}

	@Test
	public void shouldReturnEmptyStringIfNullWasFoundOnTheWay() {
		Client c = client(1L);
		assertThat((String) evaluator.get(c, "client.child.id"), is(equalTo("")));
	}

	@Test
	public void shouldReturnEmptyStringIfTheResultIsNull() {
		Client c = client(null);
		assertThat((String) evaluator.get(c, "client.id"), is(equalTo("")));
	}

	@Test
	public void shouldInvokeAGetterDeclaredOnSuperClass() {
		Client c = vipClient(1L);
		assertThat((Long) evaluator.get(c, "client.id"), is(equalTo(1L)));
	}

	@Test
	public void shouldInvokeAIsDeclaredOnSuperClass() {
		Client c = vipClient(1L);
		c.ugly = true;
		assertThat((Boolean) evaluator.get(c, "client.ugly"), is(equalTo(true)));
	}

	private Client client(Long id) {
		return new Client(id);
	}

	private VipClient vipClient(Long id) {
		return new VipClient(id);
	}

}
