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

package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class VRaptorRequestTest {

	private Mockery mockery;
	private HttpServletRequest request;
	private VRaptorRequest vraptor;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.request = mockery.mock(HttpServletRequest.class);
		final Hashtable<String, String> t = new Hashtable<String, String>();
		t.put("name", "guilherme");
		t.put("age", "27");
		mockery.checking(new Expectations() {
			{
				allowing(request).getParameterNames();
				will(returnValue(t.keys()));
				allowing(request).getParameterMap();
				will(returnValue(t));
				allowing(request).getParameter("name");
				will(returnValue("guilherme"));
				allowing(request).getParameter("minimum");
				will(returnValue(null));
			}
		});
		this.vraptor = new VRaptorRequest(request);
	}

	@Test
	public void allowsParametersToBeOverriden() {
		vraptor.setParameter("name", "silveira");
		assertThat(vraptor.getParameter("name"), is(equalTo("silveira")));
		mockery.assertIsSatisfied();
	}

	@Test
	public void searchesOnTheFallbackRequest() {
		assertThat(vraptor.getParameter("name"), is(equalTo("guilherme")));
		mockery.assertIsSatisfied();
	}

	@Test
	public void searchesOnAddedParameters() {
		vraptor.setParameter("minimum", "12");
		assertThat(vraptor.getParameter("minimum"), is(equalTo("12")));
		mockery.assertIsSatisfied();
	}

	@Test
	public void returnsNullIfNotFound() {
		assertThat(vraptor.getParameter("minimum"), is(nullValue()));
		mockery.assertIsSatisfied();
	}

	@Test
	public void supportsGreaterLengthArrays() {
		String[] values = new String[] {"guilherme","silveira"};
		vraptor.setParameter("name", values);
		assertThat(vraptor.getParameterValues("name"), is(equalTo(values)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void returnsAllNamesOnlyOnce() {
		vraptor.setParameter("name", "silveira");
		vraptor.setParameter("size", "m");
		Enumeration<?> enumeration = vraptor.getParameterNames();
		boolean nameFound = false;
		boolean ageFound = false;
		boolean sizeFound = false;
		while(enumeration.hasMoreElements()) {
			Object value = enumeration.nextElement();
			if(value.equals("age")) {
				ageFound=true;
			} else if(value.equals("name")) {
				nameFound = true;
			} else if(value.equals("size")) {
				sizeFound = true;
			}
		}
		assertThat(nameFound, is(equalTo(true)));
		assertThat(ageFound, is(equalTo(true)));
		assertThat(sizeFound, is(equalTo(true)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void returnsBothMapsWithFirstOverridingSecond() {
		vraptor.setParameter("name", "silveira");
		vraptor.setParameter("size", "m");
		Map<?,?> map = vraptor.getParameterMap();
		assertThat((String[])map.get("name"), is(equalTo(new String[] {"silveira"})));
		assertThat((String[])map.get("size"), is(equalTo(new String[] {"m"})));
		assertThat((String)map.get("age"), is(equalTo("27")));
		mockery.assertIsSatisfied();
	}

}
