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
		final Hashtable t = new Hashtable();
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
		Enumeration enumeration = vraptor.getParameterNames();
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
		Map<String,Object> map = vraptor.getParameterMap();
		assertThat((String[])map.get("name"), is(equalTo(new String[] {"silveira"})));
		assertThat((String[])map.get("size"), is(equalTo(new String[] {"m"})));
		assertThat((String)map.get("age"), is(equalTo("27")));
		mockery.assertIsSatisfied();
	}

}
