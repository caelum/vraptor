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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.DvdDao;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.Dvd;
import br.com.caelum.vraptor.mydvds.model.DvdRental;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Test class for DvdController.
 * @author Lucas Cavalcanti
 *
 */
public class DvdsRentalsControllerTest {


	private Mockery mockery;
	private UserInfo userInfo;
	private MockResult result;
	private DvdDao dao;
	private DvdRentalsController controller;
	private UserDao userDao;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();

		dao = mockery.mock(DvdDao.class);
		userDao = mockery.mock(UserDao.class);

		mockery.checking(new Expectations() {
			{
				ignoring(userDao);
			}
		});

		userInfo = new UserInfo();
		User user = new User();
		userInfo.login(user);

		result = new MockResult();
		Validator validator = new MockValidator();

		controller = new DvdRentalsController(dao, userDao, userInfo, result, validator);
	}

	@Test
	public void addingAValidDvd() throws Exception {
		Dvd dvd = new Dvd();
		dvd.setId(2l);

		willAddTheDvdRental(dvd);

		controller.addToMyList(userInfo.getUser(), dvd);

	}
	@Test(expected=ValidationException.class)
	public void addingAnDvdCopyWhenUserIsNotLoggedIn() throws Exception {
		Dvd dvd = new Dvd();
		dvd.setId(2l);

		willNotAddTheDvdRental(dvd);

		User user = new User();
		user.setLogin("abbb");
		controller.addToMyList(user, dvd);

	}

	private void willNotAddTheDvdRental(final Dvd dvd) {
		mockery.checking(new Expectations() {
			{
				never(dao).add(with(any(DvdRental.class)));
			}
		});
	}

	private void willAddTheDvdRental(final Dvd dvd) {
		mockery.checking(new Expectations() {
			{
				one(dao).add(with(any(DvdRental.class)));
			}
		});
	}
}
