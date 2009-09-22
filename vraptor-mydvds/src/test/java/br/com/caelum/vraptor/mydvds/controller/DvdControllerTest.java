package br.com.caelum.vraptor.mydvds.controller;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.DvdDao;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.Dvd;
import br.com.caelum.vraptor.mydvds.model.DvdType;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

/**
 * Test class for DvdController.
 * @author Lucas Cavalcanti
 *
 */
public class DvdControllerTest {


	private Mockery mockery;
	private UserDao userDao;
	private HttpSession session;
	private UserInfo userInfo;
	private MockResult result;
	private DvdDao dao;
	private DvdController controller;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();

		userDao = mockery.mock(UserDao.class);
		dao = mockery.mock(DvdDao.class);
		session = mockery.mock(HttpSession.class);

		//ignoring session
		mockery.checking(new Expectations() {
			{
				allowing(session).getAttribute(UserInfo.CURRENT_USER);
				will(returnValue(null));

				ignoring(session);
				ignoring(userDao);
			}
		});

		userInfo = new UserInfo(session);
		userInfo.login(new User());

		result = new MockResult();
		Validator validator = new MockValidator();

		controller = new DvdController(dao, userDao, userInfo, result, validator);
	}

	@Test
	public void addingAValidDvd() throws Exception {
		Dvd dvd = new Dvd();
		dvd.setDescription("A random description");
		dvd.setTitle("Once upon a time");
		dvd.setType(DvdType.VIDEO);

		willAddTheDvd(dvd);

		controller.add(dvd, null);

	}

	private void willAddTheDvd(final Dvd dvd) {
		mockery.checking(new Expectations() {
			{
				one(dao).add(dvd);
			}
		});
	}
}
