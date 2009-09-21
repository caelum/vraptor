package br.com.caelum.vraptor.mydvds.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.DaoFactory;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * A classe Home, nome do componente "login". Será interceptada pelo <code>DaoInterceptor</code>.
 *
 * <code>@Component</code> não especifica nenhum nome, então aplica a regra para o nome da lógica:
 * nome da classe igual nome da classe com letras minúscúlas tirando Component, Logic, Action ou Command
 * LoginComponent >>> login
 */
@Resource
public class LoginController {

	private final DaoFactory factory;
    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;

	/**
	 * Cria o componente e injeta a fábrica de daos pelo construtor.
	 *
	 * Podemos usar injeção pelo construtor por causa do
	 * <code>DaoInterceptor.class</code> que criar e ejetar a fábrica.
	 *
	 * @param factory
	 */
	public LoginController(Result result, Validator validator, DaoFactory factory, UserInfo userInfo) {
	    this.result = result;
	    this.validator = validator;
		this.factory = factory;
        this.userInfo = userInfo;
	}

	/**
	 * This method receives two parameters from request: login and password.
	 */
	@Path("/login")
	@Post
	public void login(String login, String password) {
		// search for the user in the database
		final User currentUser = factory.getUserDao().search(login, password);

		// if no user is found, adds an error message to the validator
		// "invalid_login_or_password" is the message key from messages.properties,
		// and that key is used with the fmt taglib in index.jsp, for example: <fmt:message key="error.key">
		validator.checking(new Validations() {{
		    that(currentUser, is(notNullValue()), "login", "invalid_login_or_password");
		}});
		validator.onErrorUse(Results.page()).forward("index.jsp");

		// the login was valid, add user to session
		userInfo.login(currentUser);
		result.use(Results.logic()).redirectTo(UserController.class).home();
	}

	@Path("/logout")
	public void logout() {
	    userInfo.logout();
	    result.use(Results.logic()).redirectTo(LoginController.class).index();
	}

	@Path("/")
	public void index() {
	}

}
