package br.com.caelum.vraptor.mydvds.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.validator.Hibernate;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * The resource <code>UsersController</code> handles all user operations,
 * such as adding new users, listing all users, and so on.
 */
@Resource
public class UsersController {

    private final Validator validator;
    private final Result result;
    private final UserInfo userInfo;
	private final UserDao dao;

	/**
	 * Receives dependencies through the constructor.
	 * @param factory dao factory.
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 */
	public UsersController(UserDao dao, UserInfo userInfo, Result result, Validator validator) {
		this.dao = dao;
		this.result = result;
		this.validator = validator;
        this.userInfo = userInfo;
	}

	/**
	 * Accepts HTTP GET requests.
	 * URL:  /home
	 * View: /WEB-INF/jsp/user/home.jsp
	 *
	 * Shows user's home page containing his Dvd collection.
	 */
	@Path("/home")
	@Get
	public void home() {
	    dao.refresh(userInfo.getUser());
	}

	/**
     * Accepts HTTP GET requests.
     * URL:  /users (only GET requests for this URL)
     * View: /WEB-INF/jsp/user/list.jsp
     *
     * Lists all users.
     */
	@Path("/users")
	@Get
	public void list() {
        List<User> users = new ArrayList<User>();
        // search by hand example
        List<User> usersFromDatabase = this.dao.listAll();
        for (User user : usersFromDatabase) {
            User newUser = new User();
            newUser.setId(user.getId());
            newUser.setName(user.getName());
            users.add(newUser);
        }

        result.include("users", users);
    }

	/**
	 * Accepts HTTP POST requests.
	 * URL:	 /users
	 * View: /WEB-INF/jsp/user/add.jsp
	 *
	 * The "user" parameter will be populated with the request parameters, for example:
	 *
	 * POST	/user
	 * user.name=Nico
	 * user.login=555555
	 *
	 * automatically populates the name and login parameters on the user object with values Nico and 555555.
	 *
	 * Adds new users to the database.
	 */
	@Path("/users")
	@Post
	public void add(final User user) {
		// calls Hibernate Validator for the user instance
	    validator.checking(new Validations() {{
		    // checks if there is already an user with the specified login
		    boolean loginDoesNotExist = !dao.containsUserWithLogin(user.getLogin());
		    that(loginDoesNotExist, "login", "login_already_exists");

		    and(Hibernate.validate(user));// will return all errors in a collection
		}});

		// redirects to the index page if any validation errors occur.
		validator.onErrorUse(Results.page()).of(HomeController.class).index();
		this.dao.add(user);
		result.use(Results.logic()).redirectTo(UsersController.class).userAdded(user);
	}

	/**
	 * Shows the page with information when a user is successfully added.
	 */
	@Get
	public void userAdded(User user) {
	    result.include("user", user);
	}

	/**
	 * Accepts HTTP GET requests.
	 * URL:  /users/id (for example, /users/42 shows information on the user with id 42)
	 * View: /WEB-INF/jsp/user/view.jsp
	 *
	 * Shows information on the specified user.
	 * @param user
	 */
	@Path("/users/{user.id}")
	@Get
	public void view(User user) {
	    this.dao.refresh(user);
	    result.include("user", user);
	}

}
