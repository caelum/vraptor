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

package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.Arrays;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.view.DefaultValidationViewsFactory;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.Results;

public class DefaultValidatorTest {

	private VRaptorMockery mockery;
	private Result result;
	private DefaultValidator validator;
	private LogicResult logicResult;
	private MyComponent instance;
	private Proxifier proxifier;
	private PageResult pageResult;
	private Outjector outjector;


	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.proxifier = new DefaultProxifier();
		this.result = mockery.mock(Result.class);
		this.logicResult = mockery.mock(LogicResult.class);
		this.outjector = mockery.mock(Outjector.class);
		this.instance = new MyComponent();
		//TODO testing
		this.validator = new DefaultValidator(result, new DefaultValidationViewsFactory(result, proxifier), outjector, proxifier, null);
		this.pageResult = mockery.mock(PageResult.class);
	}

	@Test
	public void shouldDoNothingWhenYouDontSpecifyTheValidationPage() throws Exception {
		mockery.checking(new Expectations() {{
			ignoring(anything());
		}});
		validator.checking(new Validations() {{
			that(false, "", "");
		}});
	}

	@Test
	public void redirectsToCustomOnErrorPage() {
		try {
			mockery.checking(new Expectations() {
				{
					one(result).include((String) with(an(String.class)), with(an(ArrayList.class)));
					one(outjector).outjectRequestMap();
					one(result).use(LogicResult.class); will(returnValue(logicResult));
					one(logicResult).forwardTo(MyComponent.class); will(returnValue(instance));
				}
			});
			validator.checking(new Validations() {
				{
					that(false, "", "");
				}
			});
			validator.onErrorUse(Results.logic()).forwardTo(MyComponent.class).logic();
			Assert.fail("should stop flow");
		} catch (ValidationException e) {
			// ok, shoul still assert satisfied
			Assert.assertEquals(this.instance.run, true);
			mockery.assertIsSatisfied();
		}
	}

	@Test
	public void testThatValidatorGoToRedirectsToTheErrorPageImmediatellyAndNotBeforeThis() {
		try {
			// call all other validation methods and don't expect them to redirect
			validator.addAll(Arrays.asList(new ValidationMessage("test", "test")));
			validator.checking(new Validations(){{
				that(false, "", "");
			}});

			// now we expect the redirection
			mockery.checking(new Expectations() {{
				one(result).include((String) with(an(String.class)), with(an(ArrayList.class)));
				one(outjector).outjectRequestMap();
				one(result).use(PageResult.class);
				will(returnValue(pageResult));

				one(pageResult).of(MyComponent.class); will(returnValue(instance));
			}});

			validator.onErrorUse(Results.page()).of(MyComponent.class).logic();
			Assert.fail("should stop flow");
		} catch (ValidationException e) {
			// ok, shoul still assert satisfied
			Assert.assertEquals(this.instance.run, true);
			mockery.assertIsSatisfied();
		}
	}

	@Resource
	public static class MyComponent {
		private boolean run;

		public void logic() {
			this.run = true;
		}
	}

}
