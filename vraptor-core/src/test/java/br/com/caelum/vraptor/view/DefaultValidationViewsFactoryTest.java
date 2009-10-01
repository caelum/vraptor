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
package br.com.caelum.vraptor.view;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockedLogic;
import br.com.caelum.vraptor.util.test.MockedPage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class DefaultValidationViewsFactoryTest {


	private Mockery mockery;
	private Result result;
	private Proxifier proxifier;
	private DefaultValidationViewsFactory factory;
	private List<Message> errors;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		result = mockery.mock(Result.class);
		proxifier = new DefaultProxifier();
		factory = new DefaultValidationViewsFactory(result, proxifier);
		errors = Collections.emptyList();

	}


	public static class RandomComponent {
		public void random() {

		}
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfLogicResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(LogicResult.class);
				will(returnValue(new MockedLogic()));
			}
		});
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class).random();
	}
	@Test
	public void shouldThrowExceptionOnlyAtTheEndOfValidationCall() throws Exception {

		mockery.checking(new Expectations() {
			{
				exactly(2).of(result).use(LogicResult.class);
				will(returnValue(new MockedLogic()));
				exactly(2).of(result).use(PageResult.class);
				will(returnValue(new MockedPage()));
			}
		});
		factory.instanceFor(LogicResult.class, errors);
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class);
		factory.instanceFor(PageResult.class, errors);
		factory.instanceFor(PageResult.class, errors).of(RandomComponent.class);

		mockery.assertIsSatisfied();
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfPageResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(PageResult.class);
				will(returnValue(new MockedPage()));
			}
		});
		factory.instanceFor(PageResult.class, errors).forward("any uri");
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfEmptyResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(EmptyResult.class);
				will(returnValue(new EmptyResult()));
			}
		});
		factory.instanceFor(EmptyResult.class, errors);
	}
}
