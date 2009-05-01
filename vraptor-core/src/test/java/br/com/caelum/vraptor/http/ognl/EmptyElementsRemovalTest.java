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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ognl.EmptyElementsRemoval;

public class EmptyElementsRemovalTest {
	
	private EmptyElementsRemoval removal;
	@Before
	public void setup() {
		this.removal = new EmptyElementsRemoval();
	}
	
	@Test
	public void shouldRemoveNullElementsOutOfAnArrayList() {
		List<String> items = new ArrayList<String>();
		removal.add(items);
		items.add("value");
		items.add(null);
		items.add("value");
		removal.removeExtraElements();
		assertThat(items.size()	, is(equalTo(2)));
	}
	
	class Dog {
		private String names[];
		public void setNames(String names[]) {
			this.names = names;
		}
		public String[] getNames() {
			return names;
		}
	}
	@Test
	public void shouldRemoveNullElementsOutOfAnArray() throws SecurityException, NoSuchMethodException {
		Dog dog = new Dog();
		dog.names = new String[] {"first", null, "second", null};
		removal.add(dog.getNames(), Dog.class.getMethod("setNames", dog.names.getClass()), dog);
		removal.removeExtraElements();
		assertThat(dog.names.length, is(equalTo(2)));
	}
	
	@Test
	public void shouldIgnoreTheFirstArrayIfOverriden() throws SecurityException, NoSuchMethodException {
		Dog dog = new Dog();
		dog.names = new String[] {"first", null, "second", null, "third"};
		removal.add(dog.getNames(), Dog.class.getMethod("setNames", dog.names.getClass()), dog);
		dog.names = new String[] {"first", null, "second", null};
		removal.add(dog.getNames(), Dog.class.getMethod("setNames", dog.names.getClass()), dog);
		removal.removeExtraElements();
		assertThat(dog.names.length, is(equalTo(2)));
	}
	

	@Test
	public void shouldPruneTheFirstArrayIfTheSecondIsInADifferentInstance() throws SecurityException, NoSuchMethodException {
		Dog dog = new Dog();
		dog.names = new String[] {"first", null, "second", null, "third"};
		removal.add(dog.getNames(), Dog.class.getMethod("setNames", dog.names.getClass()), dog);
		Dog dog2 = new Dog();
		dog2.names = new String[] {"first", null, "second", null};
		removal.add(dog2.getNames(), Dog.class.getMethod("setNames", dog.names.getClass()), dog2);
		removal.removeExtraElements();
		assertThat(dog.names.length, is(equalTo(3)));
		assertThat(dog2.names.length, is(equalTo(2)));
	}
	
		

}
