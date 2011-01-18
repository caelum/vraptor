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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.TypeConverter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class ReflectionBasedNullHandlerTest {

	private OgnlContext context;
	private @Mock Container container;
	private @Mock EmptyElementsRemoval removal;
	private @Mock Converters converters;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		AbstractOgnlTestSupport.configOgnl(converters);

		this.context = (OgnlContext) Ognl.createDefaultContext(null);
		context.setTraceEvaluations(true);
		context.put("removal", removal);
		when(container.instanceFor(Converters.class)).thenReturn(converters);
		when(converters.to(String.class)).thenReturn((Converter) new StringConverter());
		when(converters.to(Long.class)).thenReturn((Converter) new LongConverter());
	}

	@Test
	public void shouldInstantiateAnObjectIfRequiredToSetAProperty() throws OgnlException {
		House house = new House();
		Ognl.setValue("mouse.name", context, house, "James");
		assertThat(house.getMouse().getName(), is(equalTo("James")));
	}

	@Test
	public void shouldInstantiateAListOfStrings() throws OgnlException {
		House house = new House();
		Ognl.setValue("mouse.eyeColors[0]", context, house, "Blue");
		Ognl.setValue("mouse.eyeColors[1]", context, house, "Green");
		verify(removal).add(anyCollection());
		assertThat(house.getMouse().getEyeColors().get(0), is(equalTo("Blue")));
		assertThat(house.getMouse().getEyeColors().get(1), is(equalTo("Green")));
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
		final TypeConverter typeConverter = mock(TypeConverter.class);
		final House house = new House();
		final Mouse tom = new Mouse();
		Method method = House.class.getDeclaredMethod("setMouse", Mouse.class);
		when(typeConverter.convertValue(context, house, method,	"mouse", "22", Mouse.class)).thenReturn(tom);

		Ognl.setTypeConverter(context, typeConverter);
		Ognl.setValue("mouse", context, house, "22");
		assertThat(house.getMouse(), is(equalTo(tom)));
	}

}
