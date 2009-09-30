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

package br.com.caelum.vraptor.extra;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ForwardToDefaultViewInterceptorTest {

	private VRaptorMockery mockery;
	private MethodInfo pageResult;
	private Result result;
	private ForwardToDefaultViewInterceptor interceptor;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery(true);
		this.pageResult = mockery.mock(MethodInfo.class);
		this.result = mockery.mock(Result.class);
		this.interceptor = new ForwardToDefaultViewInterceptor(result, pageResult);
	}

	@Test
	public void doesNothingIfResultWasAlreadyUsed() {
		mockery.checking(new Expectations() {
			{
				one(result).used(); will(returnValue(true));
			}
		});
		interceptor.intercept(null, null, null);
		mockery.assertIsSatisfied();
	}

}
