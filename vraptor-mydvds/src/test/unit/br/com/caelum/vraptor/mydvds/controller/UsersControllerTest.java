package br.com.caelum.vraptor.mydvds.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Test class for UserController
 *
 * @author Lucas Cavalcanti
 *
 */
public class UsersControllerTest {


	private Mockery mockery;
	private UserDao dao;
	private HttpSession session;
	private UserInfo userInfo;
	private UsersController controller;
	private MockResult result;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();

		dao = mockery.mock(UserDao.class);
		session = mockery.mock(HttpSession.class);

		//ignoring http session
		mockery.checking(new Expectations() {
			{
				allowing(session).getAttribute(UserInfo.CURRENT_USER);
				will(returnValue(null));

				ignoring(session);
			}
		});

		userInfo = new UserInfo(session);

		result = new MockResult();
		Validator validator = new MockValidator();

		controller = new UsersController(dao, userInfo, result, validator);
	}

	@Test
	public void listingAllUsersWillNotExposeTheirLoginAndPassword() throws Exception {
		User user = new User();
		user.setName("John");
		user.setLogin("john");
		user.setPassword("youwontknow");

		userListingWillContain(user);

		controller.list();

		// this is the way we can inspect outjected objects on result:
		// we can assign without casting, because of generic return
		List<User> users = result.included("users");
		assertThat(users.size(), is(1));
		assertThat(users.get(0).getLogin(), is("john"));
		assertThat(users.get(0).getPassword(), is(nullValue()));
		assertThat(users.get(0).getName(), is("John"));
	}

	@Test
	public void addingAValidUserWillHappenWithoutErrors() throws Exception {
		User user = new User();
		user.setLogin("mylogin");
		user.setPassword("myPassword");
		user.setName("Testing");

		ifTheUserWithThisLoginDoesntExist("mylogin");
		willAddTheUser(user);

		controller.add(user);
	}
	@Test(expected=ValidationError.class)
	public void addingAUserWithHibernateValidationErrors() throws Exception {
		User user = new User();
		user.setLogin("myLogin");
		user.setPassword("short");
		user.setName("Testing");

		ifTheUserWithThisLoginDoesntExist("myLogin");
		willNotAddTheUser(user);

		controller.add(user);
	}
	@Test(expected=ValidationError.class)
	public void addingAUserWithInvalidLogin() throws Exception {
		User user = new User();
		user.setLogin("It's invalid");
		user.setPassword("short");
		user.setName("Testing");

		ifTheUserWithThisLoginDoesntExist("It's invalid");
		willNotAddTheUser(user);

		controller.add(user);
	}
	@Test(expected=ValidationError.class)
	public void addingAUserWithAnUnavailableLogin() throws Exception {
		User user = new User();
		user.setLogin("myLogin");
		user.setPassword("myPassword");
		user.setName("Testing");

		ifTheUserWithThisLoginExists("myLogin");
		willNotAddTheUser(user);

		controller.add(user);
	}


	private void ifTheUserWithThisLoginExists(final String login) {
		mockery.checking(new Expectations() {
			{
				one(dao).containsUserWithLogin(login);
				will(returnValue(true));
			}
		});
	}

	private void willNotAddTheUser(final User user) {
		mockery.checking(new Expectations() {
			{
				never(dao).add(user);
			}
		});
	}

	private void ifTheUserWithThisLoginDoesntExist(final String login) {

		mockery.checking(new Expectations() {
			{
				one(dao).containsUserWithLogin(login);
				will(returnValue(false));
			}
		});
	}

	private void willAddTheUser(final User user) {

		mockery.checking(new Expectations() {
			{
				one(dao).add(user);
			}
		});
	}

	private void userListingWillContain(final User user) {
		mockery.checking(new Expectations() {
			{
				one(dao).listAll();
				will(returnValue(Collections.singletonList(user)));
			}
		});
	}
}
