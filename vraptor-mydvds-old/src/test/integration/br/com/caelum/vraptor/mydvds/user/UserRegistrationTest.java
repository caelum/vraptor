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
package br.com.caelum.vraptor.mydvds.user;

import org.junit.Test;

import br.com.caelum.vraptor.mydvds.IntegrationTestCase;

/**
 * Integration tests for user registration and logging in.
 *
 * It is very important that the integration tests be as readable as possible.
 * So all infrastructure for the integration tests will be on IntegrationTestCase
 * or in the Page Objects.
 *
 * @author Lucas Cavalcanti
 *
 */
public class UserRegistrationTest extends IntegrationTestCase {

	@Test
	public void registeringAnInvalidUser() throws Exception {
		openRootPage()
			.fillRegisterForm()
				.withName("I am")
				.withLogin("too")
				.withPassword("short")
				.andSubmit();
		assertContainsErrors();
	}

	@Test
	public void logginInWithAnInvalidUser() throws Exception {
		openRootPage()
			.fillLoginForm()
				.withLogin("doesnt")
				.withPassword("exist")
				.andSubmit();
		assertContainsErrors();
	}

	@Test
	public void registeringAValidUser() throws Exception {
		openRootPage()
			.fillRegisterForm()
				.withName("John Paul")
				.withLogin("johnpaul")
				.withPassword("paulie")
				.andSubmit();
		assertContainsMessage("User John Paul successfully added");

	}

	@Test
	public void logginInWithAValidUser() throws Exception {
		thereIsAUserNamed("johnny");
		openRootPage()
			.fillLoginForm()
				.withLogin("johnny")
				.withPassword("johnny")
				.andSubmit();
		assertLoggedUserIs("johnny");
	}
}
