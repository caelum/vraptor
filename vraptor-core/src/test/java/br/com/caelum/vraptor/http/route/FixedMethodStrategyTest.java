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
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.collect.Sets;

public class FixedMethodStrategyTest {

	private @Mock MutableRequest request;
	private @Mock ParametersControl control;
	private ResourceMethod list;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		list = DefaultResourceMethod.instanceFor(MyControl.class, method("list"));
	}

	@Test
	public void canTranslate() {
		FixedMethodStrategy strategy = new FixedMethodStrategy("abc", list,
				methods(HttpMethod.POST), control, 0, new String[] {});
		when(control.matches("/clients/add")).thenReturn(true);
		ResourceMethod match = strategy.resourceMethod(request, "/clients/add");
		assertThat(match, is(VRaptorMatchers.resourceMethod(method("list"))));
		verify(control, only()).fillIntoRequest("/clients/add", request);
	}

	@Test
	public void areEquals() throws Exception {
		FixedMethodStrategy first = new FixedMethodStrategy("/uri", list, EnumSet.of(HttpMethod.GET), control, 0, new String[0]);
		FixedMethodStrategy second = new FixedMethodStrategy("/uri", list, EnumSet.of(HttpMethod.GET), control, 2, new String[0]);
		FixedMethodStrategy third = new FixedMethodStrategy("/different", list, EnumSet.of(HttpMethod.GET), control, 2, new String[0]);
		FixedMethodStrategy forth = new FixedMethodStrategy("/uri", list, EnumSet.of(HttpMethod.POST), control, 2, new String[0]);

		assertThat(first, equalTo(second));
		assertThat(first, not(equalTo(third)));
		assertThat(first, not(equalTo(forth)));
	}

	private Set<HttpMethod> methods(HttpMethod method) {
		return Sets.newHashSet(method);
	}

	private Method method(String name, Class... types) {
		try {
			return MyControl.class.getDeclaredMethod(name, types);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	class Dog {
		private Long id;

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
	}

	@br.com.caelum.vraptor.Resource
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
}
