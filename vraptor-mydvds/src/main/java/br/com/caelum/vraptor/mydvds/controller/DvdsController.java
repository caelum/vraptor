package br.com.caelum.vraptor.mydvds.controller;

import static br.com.caelum.vraptor.mydvds.validation.CustomMatchers.notEmpty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.mydvds.dao.DvdDao;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.Dvd;
import br.com.caelum.vraptor.mydvds.model.User;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * The resource <code>DvdController</code> handles all Dvd operations,
 * such as adding new Dvds, listing all Dvds, and so on.
 *
 * This is a RESTful Resource, so we will explain how to use REST on
 * VRaptor 3 here.
 * POST /dvds -> adds a dvd
 *
 * GET /dvds/{id} -> shows the dvd of given id
 *
 */
@Resource
public class DvdsController {

	private static final Logger LOG = Logger.getLogger(DvdsController.class);

    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;
	private final DvdDao dao;
	private final UserDao userDao;

	/**
	 * Receives dependencies through the constructor.
	 * @param factory dao factory.
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 */
	public DvdsController(DvdDao dao, UserDao userDao, UserInfo userInfo, Result result, Validator validator) {
		this.dao = dao;
		this.userDao = userDao;
		this.result = result;
        this.validator = validator;
        this.userInfo = userInfo;
	}

	/**
	 * Accepts HTTP POST requests.
	 * URL:  /dvds
	 * View: /WEB-INF/jsp/dvd/add.jsp
	 *
	 * The method adds a new dvd and updates the user.
	 * We use POST HTTP verb when we want to create some resource.
	 *
	 * The <code>UploadedFile</code> is automatically handled
	 * by VRaptor's <code>MultipartInterceptor</code>.
	 */
	@Path("/dvds")
	@Post
	public void add(final Dvd dvd, UploadedFile file) {
	    validator.checking(new Validations() {{
		    that(dvd.getTitle(), is(notEmpty()), "login", "invalid_title");
		    that(dvd.getType(), is(notNullValue()), "name", "invalid_type");
		    that(dvd.getDescription(), is(notEmpty()), "description", "invalid_description");
		    that(dvd.getDescription().length() >= 6, "description", "invalid_description");
		}});

		validator.onErrorUse(Results.page()).of(UsersController.class).home();

		// is there a file?
		if (file != null) {
		    // usually we would save the file, in this case, we just log :)
			LOG.info("Uploaded file: " + file.getFileName());
		}

		User user = refreshUser();
		user.getDvds().add(dvd);

		dao.add(dvd);
		userDao.update(user);

		result.use(Results.logic()).redirectTo(DvdsController.class).show(dvd);
	}

	/**
	 * Accepts HTTP GET requests.
	 * URL:  /dvds/{id}
	 * View: /WEB-INF/jsp/dvd/show.jsp
	 * Shows the page with information when a Dvd is successfully added.
	 *
	 * We should only use GET HTTP verb for safe operations. For instance,
	 * showing a DVD has no side effects, so GET is fine.
	 *
	 * We can use templates for Paths, so VRaptor will automatically extract
	 * variables of the matched URI, and set the fields on parameters.
	 * In this case, GET /dvds/15 will execute the method below, and
	 * there will be a parameter dvd.id=15 on request, causing dvd.getId() equal
	 * to 15.
	 */
	@Path("/dvds/{dvd.id}")
	@Get
	public void show(Dvd dvd) {
	    result.include("dvd", dvd);
	}

    /**
     * Accepts HTTP POST requests.
     * URL:  /dvds/addToList/id (for example, /dvd/addToList/3 adds the dvd with id 3 to the user's collection)
     * View: redirects to user's home
     *
     * This method adds a dvd to a user's collection.
     */
    @Path("/dvds/addToList/{dvd.id}")
    @Post
	public void addToMyList(final Dvd dvd) {
	    final User user = refreshUser();
	    validator.checking(new Validations() {{
		    that(user.getDvds(), not(hasItem(dvd)), "dvd", "you_already_have_this_dvd");
		}});

		validator.onErrorUse(Results.page()).of(UsersController.class).home();

		user.getDvds().add(dvd);
		userDao.update(user);

		result.use(Results.logic()).redirectTo(UsersController.class).home();
	}

    /**
	 * Accepts HTTP GET requests.
	 * URL:  /dvds/search
	 * View: /WEB-INF/jsp/dvd/search.jsp
	 *
	 * Searches are not unique resources, so it is ok to use searches with
	 * query parameters.
	 *
	 * @param dvd
	 */
	@Path("/dvds/search")
	@Get
	public void search(Dvd dvd) {
        if (dvd.getTitle() == null) {
            dvd.setTitle("");
        }

        result.include("dvds", this.dao.searchSimilarTitle(dvd.getTitle()));
    }

    /**
	 * Refreshes user data from database.
	 */
    private User refreshUser() {
        User user = userInfo.getUser();
		userDao.refresh(user);
        return user;
    }

}
