package org.vraptor.mydvds.logic;

import java.util.ArrayList;
import java.util.List;

import org.vraptor.mydvds.dao.DaoFactory;
import org.vraptor.mydvds.interceptor.UserInfo;
import org.vraptor.mydvds.model.User;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.Hibernate;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * A classe NewUser, nome do componente "user". Será interceptada pelo <code>DaoInterceptor</code> e
 * <code>TransactionInterceptor</code> .
 */
@Resource
public class UserController {

	private final DaoFactory factory;
    private final Validator validator;
    private final Result result;
    private final UserInfo userInfo;

	/**
	 * Cria o componente e injeta a fábrica de daos pelo construtor.
	 *
	 * Podemos usar injeção pelo construtor por causa do
	 * <code>DaoInterceptor.class</code> que cria e ejeta a fábrica.
	 *
	 * @param factory fábrica de daos
	 */
	public UserController(Result result, DaoFactory factory, Validator validator, UserInfo userInfo) {
		this.result = result;
        this.factory = factory;
        this.validator = validator;
        this.userInfo = userInfo;
	}

	@Get
	public void home() {
	    factory.getUserDao().refresh(userInfo.getUser());
	    result.use(Results.page()).forward();
	}

	@Get
	public void list() {
        List<User> users = new ArrayList<User>();
        // search by hand example
        List<User> usersFromDatabase = this.factory.getUserDao().listAll();
        for (User user : usersFromDatabase) {
            User newUser = new User();
            newUser.setId(user.getId());
            newUser.setName(user.getName());
            users.add(newUser);
        }

        result.include("users", users);
        result.use(Results.page()).forward();
    }

	/**
	 * URL: 			/user
	 * View ok:			add.jsp
	 *
	 * The "user" parameter will be populated with the request parameters, for example:
	 *
	 * 		/user?user.name="Nico"&user.login="555555"
	 *
	 * automatically populates the name and login parameters from the user object with values Nico and 555555
	 *
	 * O método adiciona o usuário no banco de dados.
	 */
	@Path("/user")
	@Post
	public void add(User user) {
	    result.include("user", user);
	    validateAdd(user);

		this.factory.getUserDao().add(user);
		result.use(Results.page()).forward();
	}

	/**
	 * Validation with VRaptor.
	 */
	public void validateAdd(final User user) {
		validator.checking(new Validations() {{
		    // checks if there is already an user with the specified login
		    that(!factory.getUserDao().containsUserWithLogin(user.getLogin()), "login", "login_already_exists");

		    // calls Hibernate Validator for the user object
		    and(Hibernate.validate(user));
		}});

		validator.onErrorUse(Results.page()).forward("index.jsp");
	}

}
