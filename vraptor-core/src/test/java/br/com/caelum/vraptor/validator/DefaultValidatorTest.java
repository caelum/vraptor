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
package br.com.caelum.vraptor.validator;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;

public class DefaultValidatorTest {

	private VRaptorMockery mockery;
	private Result result;
	private DefaultValidator validator;
	private LogicResult logicResult;
	private MyComponent instance;
	private Proxifier proxifier;
	private PageResult pageResult;
	private MutableRequest request;


	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.proxifier = new DefaultProxifier();
		this.result = mockery.mock(Result.class);
		this.logicResult = mockery.mock(LogicResult.class);
		this.instance = new MyComponent();
		this.request = mockery.mock(MutableRequest.class);
		this.validator = new DefaultValidator(proxifier, result,request);
		this.pageResult = mockery.mock(PageResult.class);
	}

	@Test
	public void redirectsToStandardPageResultByDefault() {
		mockery.checking(new Expectations() {
			{
				String referer = "google.com";
				one(request).getRequestURI(); will(returnValue(referer));
				one(request).getContextPath(); will(returnValue(""));
				one(result).include((String) with(an(String.class)), with(an(ArrayList.class)));
				one(result).use(PageResult.class);
				will(returnValue(pageResult));
				one(pageResult).forward(referer);
			}
		});
		try {
			validator.checking(new Validations() {
				{
					that("", "", false);
				}
			});
			Assert.fail("should stop flow");
		} catch (ValidationError e) {
			// ok, shoul still assert satisfied
			mockery.assertIsSatisfied();
		}
	}

	@Test
	public void redirectsToCustomOnErrorPage() {
		try {
			mockery.checking(new Expectations() {
				{
					one(result).include((String) with(an(String.class)), with(an(ArrayList.class)));
					one(result).use(LogicResult.class); will(returnValue(logicResult));
					one(logicResult).forwardTo(MyComponent.class); will(returnValue(instance));
				}
			});
			validator.onError().goTo(MyComponent.class).logic();
			validator.checking(new Validations() {
				{
					that("", "", false);
				}
			});
			Assert.fail("should stop flow");
		} catch (ValidationError e) {
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
