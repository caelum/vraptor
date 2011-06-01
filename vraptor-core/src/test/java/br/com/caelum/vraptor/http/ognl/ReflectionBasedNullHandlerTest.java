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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.TypeConverter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;

public class ReflectionBasedNullHandlerTest {

	private OgnlContext context;
	private @Mock Container container;
	private @Mock EmptyElementsRemoval removal;
	private @Mock Converters converters;
	private Proxifier proxifier;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		AbstractOgnlTestSupport.configOgnl(converters);

        this.proxifier = new CglibProxifier(new ReflectionInstanceCreator());
		this.context = (OgnlContext) Ognl.createDefaultContext(null);
		context.setTraceEvaluations(true);
		context.put("removal", removal);
		context.put("nullHandler", new GenericNullHandler(removal));
		context.put("proxifier", proxifier);
		
		when(container.instanceFor(Converters.class)).thenReturn(converters);
		when(converters.to(String.class)).thenReturn(new StringConverter());
		when(converters.to(Long.class)).thenReturn(new LongConverter());
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


	@Test
	public void shouldFoundASetter() throws Exception {
		final House aSimpleJavaBeans = new House();
		final List<Method> methodsOfHouseClass = Arrays.asList(House.class.getMethods());
		Method foundMethod = new ReflectionBasedNullHandler(proxifier).findSetter(aSimpleJavaBeans, "Mouse", Mouse.class);
		assertThat(methodsOfHouseClass, hasItem(foundMethod));
		assertThat(foundMethod.toGenericString(), startsWith("public void "));
		assertThat(foundMethod.getName(), is(startsWith("setMouse")));
	}


	@Test
	public void shouldFoundAGetter() throws Exception {
		final House aSimpleJavaBeans = new House();
		final List<Method> methodsOfHouseClass = Arrays.asList(House.class.getMethods());
		Method foundMethod = new ReflectionBasedNullHandler(proxifier).findGetter(aSimpleJavaBeans, "Mouse");
		assertThat(methodsOfHouseClass, hasItem(foundMethod));
		assertTrue(Mouse.class.isAssignableFrom(foundMethod.getReturnType()));
		assertThat(foundMethod.getName(), is(startsWith("getMouse")));
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxify(final T pojo) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(pojo.getClass());
		enhancer.setCallbackTypes(new Class[] { Dispatcher.class});
		enhancer.setCallbacks(new Callback[] {  new Dispatcher() { public Object loadObject() throws Exception {return pojo;}	}});
		return (T) enhancer.create();
	}

	@Test
	public void shouldFoundASetterEvenWithAProxyObject() throws Exception {
		final House aSimpleJavaBeans = new House();
		House beanProxified = proxify(aSimpleJavaBeans);
		final List<Method> methodsOfHouseClass = Arrays.asList(House.class.getMethods());
		Method foundMethod = new ReflectionBasedNullHandler(proxifier).findSetter(beanProxified, "Mouse", Mouse.class);
		assertThat(methodsOfHouseClass, hasItem(foundMethod));
		assertThat(foundMethod.toGenericString(), startsWith("public void "));
		assertThat(foundMethod.getName(), is(startsWith("setMouse")));
	}


	@Test
	public void shouldFoundAGetterWithAProxyObject() throws Exception {
		final House aSimpleJavaBeans = new House();
		House beanProxified = proxify(aSimpleJavaBeans);
		final List<Method> methodsOfHouseClass = Arrays.asList(House.class.getMethods());
		Method foundMethod = new ReflectionBasedNullHandler(proxifier).findGetter(beanProxified, "Mouse");
		assertThat(methodsOfHouseClass, hasItem(foundMethod));
		assertTrue(Mouse.class.isAssignableFrom(foundMethod.getReturnType()));
		assertThat(foundMethod.getName(), is(startsWith("getMouse")));
	}

}
