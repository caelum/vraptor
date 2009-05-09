/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.eval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class EvaluatorTest {

	private Evaluator evaluator;

	@Before
	public void setup() {
		this.evaluator = new Evaluator();
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

	class TypeCreated {
		private Client client;

		public TypeCreated(Client c) {
			this.client = c;
		}

		public Client getClient() {
			return client;
		}
	}

	@Test
	public void shouldInvokeAGetter() {
		TypeCreated c = client(1L);
		assertThat((Long) evaluator.get(c, "client.id"), is(equalTo(1L)));
	}

	@Test
	public void shouldInvokeAIs() {
		TypeCreated c = client(1L);
		c.client.ugly=true;
		assertThat((Boolean) evaluator.get(c, "client.ugly"), is(equalTo(true)));
	}

	@Test
	public void shouldAccessArray() {
		TypeCreated c = client(1L);
		c.client.favoriteColors = new String[] {"blue", "red"};
		assertThat((String) evaluator.get(c, "client.favoriteColors[1]"), is(equalTo("red")));
	}

	@Test
	public void shouldAccessList() {
		TypeCreated c = client(1L);
		c.client.emails = Arrays.asList("blue", "red");
		assertThat((String) evaluator.get(c, "client.emails[1]"), is(equalTo("red")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAccessCollection() {
		TypeCreated c = client(1L);
		c.client.favoriteNumbers = new TreeSet(Arrays.asList("blue", "red"));
		assertThat((String) evaluator.get(c, "client.favoriteNumbers[1]"), is(equalTo("red")));
	}

	@Test
	public void shouldReturnEmptyStringIfNullWasFoundOnTheWay() {
		TypeCreated c = client(1L);
		assertThat((String) evaluator.get(c, "client.child.id"), is(equalTo("")));
	}

	@Test
	public void shouldReturnEmptyStringIfTheResultIsNull() {
		TypeCreated c = client(null);
		assertThat((String) evaluator.get(c, "client.id"), is(equalTo("")));
	}

	private TypeCreated client(Long id) {
		return new TypeCreated(new Client(id));
	}

}
