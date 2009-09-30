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

package br.com.caelum.vraptor.http.ognl;

import java.util.Collection;
import java.util.List;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.TypeConverter;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ReflectionBasedNullHandlerTest {

	private ReflectionBasedNullHandler handler;
	private OgnlContext context;
	private VRaptorMockery mockery;
	private Container container;
	private EmptyElementsRemoval removal;
	private Converters converters;

	@Before
	public void setup() {
		this.handler = new ReflectionBasedNullHandler();
		this.context = (OgnlContext) Ognl.createDefaultContext(null);
		context.setTraceEvaluations(true);
		this.mockery = new VRaptorMockery(true);
		this.container = mockery.mock(Container.class);
		context.put(Container.class, container);
		this.removal = mockery.mock(EmptyElementsRemoval.class);
		this.converters = mockery.mock(Converters.class);
		mockery.checking(new Expectations() {
			{
				allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
				allowing(container).instanceFor(Converters.class); will(returnValue(converters));
			}
		});
		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

	@Test
	public void shouldInstantiateAnObjectIfRequiredToSetAProperty() throws OgnlException {
		OgnlRuntime.setNullHandler(House.class, handler);
		House house = new House();
		Ognl.setValue("mouse.name", context, house, "James");
		MatcherAssert.assertThat(house.getMouse().getName(), Matchers.is(Matchers.equalTo("James")));
	}

	@Test
	public void shouldInstantiateAListOfStrings() throws OgnlException {
		mockery.checking(new Expectations() {
			{
				one(removal).add((Collection<?>)with(an(Collection.class)));
			}
		});
		OgnlRuntime.setNullHandler(House.class, handler);
		OgnlRuntime.setNullHandler(Mouse.class, handler);
		House house = new House();
		Ognl.setValue("mouse.eyeColors[0]", context, house, "Blue");
		Ognl.setValue("mouse.eyeColors[1]", context, house, "Green");
		MatcherAssert.assertThat(house.getMouse().getEyeColors().get(0), Matchers.is(Matchers.equalTo("Blue")));
		MatcherAssert.assertThat(house.getMouse().getEyeColors().get(1), Matchers.is(Matchers.equalTo("Green")));
	}

	public static class House {
		private Mouse mouse;

		public void setMouse(Mouse cat) {
			this.mouse = cat;
		}

		public Mouse getMouse() {
			return mouse;
		}

	}

	@Test
	public void shouldNotInstantiateIfLastTerm() throws OgnlException, NoSuchMethodException {
		OgnlRuntime.setNullHandler(House.class, handler);
		final TypeConverter typeConverter = mockery.mock(TypeConverter.class);
		final House house = new House();
		final Mouse tom = new Mouse();
		mockery.checking(new Expectations() {
			{
				one(typeConverter).convertValue(context, house, House.class.getDeclaredMethod("setMouse", Mouse.class),
						"mouse", "22", Mouse.class);
				will(returnValue(tom));
			}
		});
		Ognl.setTypeConverter(context, typeConverter);
		Ognl.setValue("mouse", context, house, "22");
		MatcherAssert.assertThat(house.getMouse(), Matchers.is(Matchers.equalTo(tom)));
		mockery.assertIsSatisfied();
	}

}
