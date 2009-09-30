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

package br.com.caelum.vraptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IteratorToEnumerationAdapterTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void usesAllItems() {
		List<String> items = new ArrayList<String>();
		items.add("my");
		items.add("name");
		items.add("is...");
		Iterator<String> main = items.iterator();
		Enumeration<String> enumeration = new IteratorToEnumerationAdapter(items.iterator());
		while(main.hasNext()) {
			if(!enumeration.hasMoreElements()) {
				Assert.fail("Iterator has more elements but enumeration doesnt.");
			}
			String mainElement = main.next();
			String enumElement = enumeration.nextElement();
			assertThat(enumElement, is(equalTo(mainElement)));
		}
	}

}
