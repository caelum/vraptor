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
package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultRouterTest {
	
	private Router rules;
	private Mockery mockery;
	private VRaptorRequest request;

	@org.junit.Before
	public void setup() {
		this.mockery = new Mockery();
		this.request = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
		this.rules = new DefaultRouter();
	}
	
	class Dog{
		private Long id;
		public void setId(Long id) {
			this.id = id;
		}
		public Long getId() {
			return id;
		}
	}
	
	public static class MyControl {
		public void add(Dog object) {
		}

		public void unknownMethod() {
		}

		public void list() {
		}

		public void show(Dog dog) {
		}
	}
	
	@Test
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		rules.add(new Rules() {{
			routeFor("/clients/add").is(MyControl.class).add(null);
		}});
		assertThat(rules.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method("add",Dog.class))));
	}

	private Method method(String name, Class...types) throws SecurityException, NoSuchMethodException {
		return MyControl.class.getDeclaredMethod(name, types);
	}

	@Test
	public void usesTheFirstRegisteredRuleMatchingThePattern() throws SecurityException, NoSuchMethodException {
		rules.add(new Rules() {{
			 routeFor("/clients/add").is(MyControl.class).add(null);
			 routeFor("/clients/add").is(MyControl.class).list();
		}});
		assertThat(rules.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method("add", Dog.class))));
	}

	@Test
	public void acceptsAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		rules.add(new Rules() {{
			routeFor("/clients/add").with(HttpMethod.POST).is(MyControl.class).add(null);
		}});
		assertThat(rules.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method("add",Dog.class))));
	}

	@Test
	public void ignoresAnHttpMethodLimitedMappingRule() throws NoSuchMethodException {
		rules.add(new Rules() {{
			routeFor("/clients/add").with(HttpMethod.GET).is(MyControl.class).add(null);
		}});
		assertThat(rules.parse("/clients/add", HttpMethod.POST, request), is(nullValue()));
	}

	@Test
	public void usesTheFirstRegisteredRuleIfDifferentCreatorsWereUsed() throws SecurityException, NoSuchMethodException {
		final ResourceMethod method = mockery.mock(ResourceMethod.class);
		final Rule customRule = new Rule() {
			public boolean matches(String uri, HttpMethod method) {
				return true;
			}
			public ResourceMethod resourceMethod() {
				return method;
			}
		};
		rules.add(new ListOfRules() {
			public List<Rule> getRules() {
				return Arrays.asList(customRule);
			}
		});
		rules.add(new Rules() {{
			routeFor("/clients").is(MyControl.class).list(); // if not defined, any http method is allowed
		}});
		assertThat(rules.parse("/clients", HttpMethod.POST, request), is(equalTo(method)));
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		rules.add(new Rules() {{
			routeFor("/clients/{dog.id}").with(HttpMethod.GET).is(MyControl.class).show(null);;
		}});
		ResourceMethod method = rules.parse("/clients/45", HttpMethod.POST, request);
		assertThat(request.getParameter("dog.id"), is(equalTo("45")));
		assertThat(method, is(VRaptorMatchers.resourceMethod(method("show", Dog.class))));
		Assert.fail();
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		rules.add(new Rules() {{
			routeFor("/clients*").with(HttpMethod.POST).is(MyControl.class).unknownMethod();;
		}});
		assertThat(rules.parse("/clientsWhatever", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method("unknownMethod"))));
	}

}
