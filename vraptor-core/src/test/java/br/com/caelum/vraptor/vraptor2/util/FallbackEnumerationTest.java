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

package br.com.caelum.vraptor.vraptor2.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Enumeration;
import java.util.Vector;

import org.junit.Test;

public class FallbackEnumerationTest {
	@Test
	public void shouldConcatenateBothLists() {
		Vector<String> v1 = new Vector<String>();
		v1.add("guilherme");
		Vector<String> v2 = new Vector<String>();
		v2.add("silveira");
		Enumeration<String> enumeration = new FallbackEnumeration(v1.elements(), v2.elements());
		assertThat(enumeration.hasMoreElements(), is(equalTo(true)));
		assertThat(enumeration.nextElement(), is(equalTo(v1.get(0))));
		assertThat(enumeration.hasMoreElements(), is(equalTo(true)));
		assertThat(enumeration.nextElement(), is(equalTo(v2.get(0))));
	}
}
