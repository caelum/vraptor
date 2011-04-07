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

package br.com.caelum.vraptor.http.route;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class RulesTest {

	private VRaptorMockery mockery;
	private Router router;
	private Proxifier proxifier;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.router = mockery.mock(Router.class);
		this.proxifier = mockery.mock(Proxifier.class);
		mockery.checking(new Expectations() {
			{
				one(router).builderFor("");
				will(returnValue(new DefaultRouteBuilder(proxifier, null, null, null, new JavaEvaluator(), "")));
			}
		});
	}

	@Test(expected=IllegalRouteException.class)
	public void allowsAdditionOfRouteBuildersByDefaultWithNoStrategy() {
		new Rules(router) {
			@Override
			public void routes() {
				routeFor("");
			}
		};
		mockery.assertIsSatisfied();
	}

}
