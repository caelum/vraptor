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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ioc.Container;

public class GenericNullHandlerTest {

	private @Mock EmptyElementsRemoval removal;
	private @Mock Container container;
	private GenericNullHandler handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		handler = new GenericNullHandler(removal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostInterfaces() throws Exception {
		handler.instantiate(TheInterface.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostAbstractClasses() throws Exception {
		handler.instantiate(AbstractClass.class);
	}

	@Test
	public void shouldCreateArrays() throws Exception {
		Long[] array = handler.instantiate(Long[].class);
		assertThat(array, is(notNullValue()));
	}

	@Test
	public void shouldInstantiateGregorianCalendarForAbstractCalendarType()
			throws Exception {
		Calendar calendar = handler.instantiate(Calendar.class);
		assertThat(calendar, is(notNullValue()));
		assertThat(calendar, is(instanceOf(GregorianCalendar.class)));
	}

	@Test
	public void shouldInstantiateArrayListForCollectionInterface()
			throws Exception {
		Collection<?> collection = handler.instantiate(Collection.class);
		assertThat(collection, is(notNullValue()));
		assertThat(collection, is(instanceOf(ArrayList.class)));
		verify(removal, times(1)).add(new ArrayList<Object>());
	}

	@Test
	public void shouldInstantiateArrayListForListInterface() throws Exception {
		List<?> list = handler.instantiate(List.class);
		assertThat(list, is(notNullValue()));
		assertThat(list, is(instanceOf(ArrayList.class)));
		verify(removal, times(1)).add(new ArrayList<Object>());
	}

	@Test
	public void shouldInstantiateLinkedListForQueueInterface() throws Exception {
		Queue<?> queue = handler.instantiate(Queue.class);
		assertThat(queue, is(notNullValue()));
		assertThat(queue, is(instanceOf(LinkedList.class)));
		verify(removal, times(1)).add(new LinkedList<Object>());
	}

	@Test
	public void shouldInstantiateHashSetListForSetInterface() throws Exception {
		Set<?> set = handler.instantiate(Set.class);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(HashSet.class)));
		verify(removal, times(1)).add(new HashSet<Object>());
	}

	@Test
	public void shouldInstantiateTreeSetListForSortedSetInterface()
			throws Exception {
		Set<?> set = handler.instantiate(SortedSet.class);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(TreeSet.class)));
		verify(removal, times(1)).add(new TreeSet<Object>());
	}

}

interface TheInterface {
}

abstract class AbstractClass {
}
