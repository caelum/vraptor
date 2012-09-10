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
package br.com.caelum.vraptor.test;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.SimpleNode;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.legacy.ClassImposteriser;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class VRaptorMockery {

	private int count = 0;

	private final Mockery mockery;

	public VRaptorMockery() {
		this(false);
	}

	public VRaptorMockery(boolean supportConcreteClasses) {
		mockery = new Mockery();
		if (supportConcreteClasses) {
			mockery.setImposteriser(ClassImposteriser.INSTANCE);
		}
	}


	public Sequence sequence(String name) {
		return mockery.sequence(name);
	}

	public void assertIsSatisfied() {
		mockery.assertIsSatisfied();
	}

	public void checking(ExpectationBuilder expectations) {
		mockery.checking(expectations);
	}

	public <T> T mock(Class<T> typeToMock) {
		return mockery.mock(typeToMock);
	}

	public <T> ResourceClass resource(final Class<T> type) {
		final ResourceClass resource = mockery.mock(ResourceClass.class, "resource : " + type + (++count));
		mockery.checking(new Expectations() {
			{
				allowing(resource).getType();
				will(returnValue(type));
			}
		});
		return resource;
	}

	/**
	 * Mocks a type and says its local name for better error output.
	 */
	public <T> T mock(Class<T> type, String name) {
		return mockery.mock(type,name);
	}
}
