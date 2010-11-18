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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class GenericNullHandlerTest {

	private VRaptorMockery mockery;
	private EmptyElementsRemoval removal;
	private Container container;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery(true);
		this.removal = mockery.mock(EmptyElementsRemoval.class);
		this.container = mockery.mock(Container.class);
		mockery.checking(new Expectations() {
			{
				allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
			}
		});
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostInterfaces() throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		handler.instantiate(TheInterface.class, container);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostAbstractClasses() throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		handler.instantiate(AbstractClass.class, container);
	}

	@Test
	public void shouldCreateArrays() throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		Long[] array = handler.instantiate(Long[].class, container);
		assertThat(array, is(notNullValue()));
	}

	@Test
	public void shouldInstantiateGregorianCalendarForAbstractCalendarType()
			throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		Calendar calendar = handler.instantiate(Calendar.class, container);
		assertThat(calendar, is(notNullValue()));
		assertThat(calendar, is(instanceOf(GregorianCalendar.class)));
	}

	@Test
	public void shouldInstantiateArrayListForCollectionInterface()
			throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new ArrayList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Collection<?> collection = handler.instantiate(Collection.class, container);
		assertThat(collection, is(notNullValue()));
		assertThat(collection, is(instanceOf(ArrayList.class)));
	}

	@Test
	public void shouldInstantiateArrayListForListInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new ArrayList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		List<?> list = handler.instantiate(List.class, container);
		assertThat(list, is(notNullValue()));
		assertThat(list, is(instanceOf(ArrayList.class)));
	}

	@Test
	public void shouldInstantiateLinkedListForQueueInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new LinkedList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Queue<?> queue = handler.instantiate(Queue.class, container);
		assertThat(queue, is(notNullValue()));
		assertThat(queue, is(instanceOf(LinkedList.class)));
	}

	@Test
	public void shouldInstantiateHashSetListForSetInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new HashSet<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Set<?> set = handler.instantiate(Set.class, container);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(HashSet.class)));
	}

	@Test
	public void shouldInstantiateTreeSetListForSortedSetInterface()
			throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new TreeSet<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Set<?> set = handler.instantiate(SortedSet.class, container);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(TreeSet.class)));
	}

}

interface TheInterface {
}

abstract class AbstractClass {
}
