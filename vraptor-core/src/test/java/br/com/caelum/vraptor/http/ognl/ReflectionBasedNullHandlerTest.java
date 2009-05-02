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

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

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
				one(removal).add((Collection)with(an(Collection.class)));
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
