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
package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.test.VRaptorMockery;


public class PatternBasedStrategyTest {
	

	private VRaptorMockery mockery;
	private MutableRequest request;
	private ParametersControl control;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.request = mockery.mock(MutableRequest.class);
		this.control = mockery.mock(ParametersControl.class);
	}
	
	@Test
	public void canHandleTypesWhichAreAvailableThroughItsPattern() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType(MyComponent.class.getPackage().getName()+".{_logic}"), new PatternBasedType("list"), null);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(true)));
	}
	
	@Test
	public void cannotHandleTypeWhenItsAnotherType() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType("Another"), new PatternBasedType("list"), null);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(false)));
	}

	@Test
	public void cannotHandleTypeWhenItsAnotherMethod() throws SecurityException, NoSuchMethodException {
		PatternBasedStrategy strategy = new PatternBasedStrategy(control, new PatternBasedType(MyComponent.class.getName()), new PatternBasedType("other"), null);
		assertThat(strategy.canHandle(MyComponent.class, MyComponent.class.getDeclaredMethod("list")), is(equalTo(false)));
	}
	
	class MyComponent {
		public void list() {
		}
		public void other() {
		}
	}

}
