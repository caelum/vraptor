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
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.HttpMethod;

public class DefaultLocatorRulesTest {
	
	private LocatorRules rules;

	public void setup() {
		this.rules = new DefaultLocatorRules();
	}
	
	class Dog{
		private Long id;
		public void setId(Long id) {
			this.id = id;
		}
		public Long getId() {
			return id;
		}
	}
	
	class MyControl {
		public void add(Dog object) {
		}

		public void unknownMethod() {
		}

		public void list() {
		}

		public void show(Dog dog) {
		}
	}
	
	public void acceptsASingleMappingRule() throws SecurityException, NoSuchMethodException {
		rules.add(new Rules() {{
			accept(MyControl.class).add(null); as("/clients/add");
		}});
		assertThat(rules.parse("/clients/add"), is(VRaptorMatchers.resourceMethod(method("add"))));
	}

	private Method method(String name, Class...types) throws SecurityException, NoSuchMethodException {
		return MyControl.class.getDeclaredMethod(name, types);
	}

	public void usesTheFirstRegisteredRuleMatchingThePattern() {
		rules.add(new Rules() {{
			accept(MyControl.class).add(null); as("/clients/add");
			accept(MyControl.class).list(); as("/clients/add");
		}});
	}

	public void acceptsAnHttpMethodLimitedMappingRule() {
		rules.add(new Rules() {{
			accept(MyControl.class).add(null); as("/clients/add", HttpMethod.POST);
		}});
	}

	public void usesTheFirstRegisteredRuleIfDifferentCreatorsWereUsed() {
		rules.add(new Rules() {{
			accept(MyControl.class).list(); as("/clients"); // if not defined, any http method is allowed
		}});
		// rules.add(new DefaultPublicMethodNotAnnotatedRules());
	}

	public void registerExtraParametersFromAcessedUrl() {
		rules.add(new Rules() {{
			accept(MyControl.class).show(null); as("/clients/{client.id}", HttpMethod.GET);
		}});
	}

	public void worksWithBasicRegexEvaluation() {
		rules.add(new Rules() {{
			accept(MyControl.class).unknownMethod(); as("/clients*", HttpMethod.POST);
		}});
	}

}
