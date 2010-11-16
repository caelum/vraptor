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
package br.com.caelum.vraptor.mydvds.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Test class for HomeController
 *
 * When testing a controller, we don't have to use the actual implementations for
 * its dependencies. So we'll mock all the dependencies using the JMock framework.
 *
 * @author Lucas Cavalcanti
 *
 */
public class HomeControllerTest {


	private Mockery mockery;
	private UserDao dao;
	private UserInfo userInfo;
	private HomeController controller;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();

		dao = mockery.mock(UserDao.class);

		userInfo = new UserInfo();

		// for Result and Validator, there are helpful mocked implementations
		// that you can use if you want to just ignore them or perform simple
		// assertions
		Result result = new MockResult();
		Validator validator = new MockValidator();

		controller = new HomeController(dao, userInfo, result, validator);
	}

	@Test
	public void logoutMustRemoveTheUserFromHttpSession() throws Exception {
		User user = new User();

		userInfo.login(user);
		assertThat(userInfo.getUser(), is(user));

		controller.logout();
		assertThat(userInfo.getUser(), is(nullValue()));
	}

	@Test
	public void loginWithAValidUserMustPutUserOnHttpSession() throws Exception {
		User user = new User();

		ifUserOnDaoIs(user);

		controller.login("valid", "user");
		assertThat(userInfo.getUser(), is(user));
	}

	@Test(expected=ValidationException.class)
	public void loginWithAnInvalidUserMustNotPutUserOnHttpSession() throws Exception {

		ifUserOnDaoIs(null);

		controller.login("invalid", "user");
		assertThat(userInfo.getUser(), is(nullValue()));
	}

	private void ifUserOnDaoIs(final User user) {

		mockery.checking(new Expectations() {
			{
				one(dao).find(with(any(String.class)), with(any(String.class)));
				will(returnValue(user));
			}
		});
	}

}
