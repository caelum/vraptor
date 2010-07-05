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

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;


@Deprecated
public class PatternBasedStrategyTest {


	private VRaptorMockery mockery;
	private ParametersControl control;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.control = mockery.mock(ParametersControl.class);
	}

	@Test
	public void canHandleTypesWhichAreAvailableThroughItsPattern() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType(MyComponent.class.getPackage().getName()+".{_logic}"),
				new PatternBasedType("list"), EnumSet.noneOf(HttpMethod.class), 0);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(true)));
	}

	@Test
	public void cannotHandleTypeWhenItsAnotherType() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType("Another"),
				new PatternBasedType("list"), EnumSet.noneOf(HttpMethod.class), 0);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(false)));
	}

	@Test
	public void cannotHandleTypeWhenItsAnotherMethod() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType(MyComponent.class.getName()),
				new PatternBasedType("other"), EnumSet.noneOf(HttpMethod.class), 0);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(false)));
	}

	class MyComponent {
		public void list() {
		}
		public void other() {
		}
	}

}
