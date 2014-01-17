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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.Ognl;
import ognl.OgnlException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;

public class VRaptorConvertersAdapterTest {

	private @Mock Converters converters;
	private @Mock Converter converter;
	private VRaptorConvertersAdapter adapter;
	private Cat myCat;
	private ResourceBundle bundle;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	bundle = ResourceBundle.getBundle("messages");
	adapter = new VRaptorConvertersAdapter(converters, bundle);
	myCat = new Cat();
	}

	public static class Cat {
	private int length;
	private Tail tail;
	private List<Leg> legs = new ArrayList<Leg>();

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public void setTail(Tail tail) {
		this.tail = tail;
	}

	public Tail getTail() {
		return tail;
	}

	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}

	public List<Leg> getLegs() {
		return legs;
	}

	}

	public static class Leg {
	@SuppressWarnings("unused")
	private final int length;

	public Leg(int length) {
		this.length = length;
	}
	}

	public static class Tail {
	public Tail(int l) {
		length = l;
	}

	private final int length;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldInvokePrimitiveConverter() throws OgnlException {
		when(converters.to(int.class)).thenReturn(converter);
		when(converter.convert("2", int.class, bundle)).thenReturn(2);
	
	Map<?,?> context = Ognl.createDefaultContext(myCat);
	Ognl.setTypeConverter(context, adapter);
	Ognl.setValue("length", context, myCat, "2");
	assertThat(myCat.length, is(equalTo(2)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldInvokeCustomTypeConverter() throws OgnlException {
	when(converters.to(Tail.class)).thenReturn(converter);
	when(converter.convert("15", Tail.class, bundle)).thenReturn(new Tail(15));
		
	Map<?,?> context = Ognl.createDefaultContext(myCat);
	Ognl.setTypeConverter(context, adapter);
	Ognl.setValue("tail", context, myCat, "15");
	assertThat(myCat.tail.length, is(equalTo(15)));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoConverterIsFound() throws Throwable {
	when(converters.to(Tail.class)).thenReturn(null);
	
	Map<?,?> context = Ognl.createDefaultContext(myCat);
	Ognl.setTypeConverter(context, adapter);
	try {
		Ognl.setValue("tail", context, myCat, "15");
	} catch (OgnlException e) {
		throw e.getCause();
	}
	}
}
