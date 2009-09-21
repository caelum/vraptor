package br.com.caelum.vraptor.mydvds.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * This class will be responsible to login/logout users.
 * We will use VRaptor URI conventions for this class.
 */
@Resource
public class LoginController {

    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;
	private final UserDao dao;

	/**
	 * You can receive any dependency on constructor. If VRaptor knows all dependencies, this
	 * class will be created with no problem. You can use as dependencies:
	 * - all VRaptor components, e.g {@link Result} and {@link Validator}
	 * - all of your classes annotated with @Component, e.g {@link UserDao}
	 * - all of the classes that have a {@link ComponentFactory}, e.g {@link Session} or {@link SessionFactory}
	 */
	public LoginController(UserDao dao, UserInfo userInfo, Result result, Validator validator) {
	    this.dao = dao;
		this.result = result;
	    this.validator = validator;
        this.userInfo = userInfo;
	}

	/**
	 * We should not provide direct access to jsps, so we need to have an empty method
	 * for redirecting to jsp. In this case we will use the root URI, which will be
	 * redirected to jsp /WEB-INF/jsp/login/index.jsp
	 */
	@Path("/")
	public void index() {
	}

	/**
	 * This method receives two parameters from request: login and password.
	 */
	@Post
	@Path("/login")
	public void login(String login, String password) {
		// search for the user in the database
		final User currentUser = dao.search(login, password);

		// if no user is found, adds an error message to the validator
		// "invalid_login_or_password" is the message key from messages.properties,
		// and that key is used with the fmt taglib in index.jsp, for example: <fmt:message key="error.key">
		validator.checking(new Validations() {{
		    that(currentUser, is(notNullValue()), "login", "invalid_login_or_password");
		}});
		validator.onErrorUse(Results.page()).of(LoginController.class).index();

		// the login was valid, add user to session
		userInfo.login(currentUser);
		result.use(Results.logic()).redirectTo(UserController.class).home();
	}

	@Path("/logout")
	public void logout() {
	    userInfo.logout();
	    result.use(Results.logic()).redirectTo(LoginController.class).index();
	}

}
