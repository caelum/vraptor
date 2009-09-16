package org.vraptor.mydvds.logic;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.vraptor.mydvds.validation.CustomMatchers.notEmpty;

import org.apache.log4j.Logger;
import org.vraptor.mydvds.dao.DaoFactory;
import org.vraptor.mydvds.interceptor.UserInfo;
import org.vraptor.mydvds.model.Dvd;
import org.vraptor.mydvds.model.User;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * The resource <code>DvdController</code> handles all Dvd operations,
 * such as adding new Dvds, listing all Dvds, and so on.
 */
@Resource
public class DvdController {

	private static final Logger LOG = Logger.getLogger(DvdController.class);

	private DaoFactory factory;
    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;

	/**
	 * Receives dependencies through the constructor.
	 *
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 * @param factory dao factory.
	 * @param userInfo info on the logged user.
	 */
	public DvdController(Result result, Validator validator, DaoFactory factory, UserInfo userInfo) {
		this.result = result;
        this.validator = validator;
        this.factory = factory;
        this.userInfo = userInfo;
	}

	/**
	 * Accepts HTTP GET requests.
	 * URL:  /dvds/search
	 * View: /WEB-INF/jsp/dvd/search.jsp
	 *
	 * This method searches for dvds based on a title.
	 *
	 * @param dvd
	 */
	@Path("/dvds/search")
	@Get
	public void search(Dvd dvd) {
        if (dvd.getTitle() == null) {
            dvd.setTitle("");
        }

        result.include("dvds", this.factory.getDvdDao().searchSimilarTitle(dvd.getTitle()));
        result.use(Results.page()).forward();
    }

	/**
	 * Accepts HTTP POST requests.
	 * URL:  /dvds
	 * View: /WEB-INF/jsp/dvd/add.jsp
	 *
	 * The method adds a new dvd and updates the user.
	 *
	 * The <code>UploadedFile</code> is automatically handled
	 * by VRaptor's <code>MultipartInterceptor</code>.
	 */
	@Path("/dvds")
	@Post
	public void add(Dvd dvd, UploadedFile file) {
	    if (dvd == null) {
	        return;
	    }

	    validateAdd(dvd);

		// is there a file?
		if (file != null) {
		    // usually we would save the file, in this case, we just log :)
			LOG.info("Uploaded file: " + file.getFileName());
		}

		User user = refreshUser();
		user.getDvds().add(dvd);

		factory.getDvdDao().add(dvd);
		factory.getUserDao().update(user);

		result.use(Results.logic()).redirectTo(DvdController.class).show(dvd);
	}

	/**
	 * Shows the page with information when a Dvd is successfully added.
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
	public void addToMyList(Dvd dvd) {
	    User user = refreshUser();
	    validateAddToMyList(dvd, user);

		user.getDvds().add(dvd);
		factory.getUserDao().update(user);

		redirectToHome();
	}

    /**
     * Validates dvd data.
     */
    private void validateAdd(final Dvd dvd) {
        validator.checking(new Validations() {{
            that(dvd.getTitle(), is(notEmpty()), "login", "invalid_title");
            that(dvd.getType(), is(notNullValue()), "name", "invalid_type");
            that(dvd.getDescription(), is(notEmpty()), "description", "invalid_description");
            that(dvd.getDescription().length() >= 6, "description", "invalid_description");
        }});

        redirectOnError();
    }

    /**
     * Checks if the user already has the added dvd
     * @param dvd
     * @param user
     */
    private void validateAddToMyList(final Dvd dvd, final User user) {
        validator.checking(new Validations() {{
            that(user.getDvds().contains(dvd), is(equalTo(false)), "dvd", "you_already_have_this_dvd");
        }});

        redirectOnError();
    }

    /**
     * Redirects to home if there are validation errors.
     */
    private void redirectOnError() {
        validator.onErrorUse(Results.page()).of(UserController.class).home();
    }

    /**
     * Redirects to home.
     */
    private void redirectToHome() {
        result.use(Results.logic()).redirectTo(UserController.class).home();
    }

	/**
	 * Refreshes user data from database.
	 */
    private User refreshUser() {
        User user = userInfo.getUser();
		factory.getUserDao().refresh(user);
        return user;
    }

}
