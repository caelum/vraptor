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

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;

/**
 * O <code>MultipartRequestInterceptor</code> vem com VRaptor e serve para facilitar o upload de arquivos.
 * Verifique o tutorial sobre o upload.
 */
@Resource
public class DvdController {

	private static final Logger LOG = Logger.getLogger(DvdController.class);

	private DaoFactory factory;
    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;

	/**
	 * Cria o componente e injeta a fábrica de daos pelo construtor.
	 *
	 * Podemos usar injeção pelo construtor por causa do
	 * <code>DaoInterceptor.class</code> que cria e ejeta a fábrica.
	 *
	 * @param factory fábrica de daos
	 */
	public DvdController(Result result, Validator validator, DaoFactory factory, UserInfo userInfo) {
		this.result = result;
        this.validator = validator;
        this.factory = factory;
        this.userInfo = userInfo;
	}

	@Path("/dvd/search")
	public void search(Dvd dvd) {
        if (dvd.getTitle() == null) {
            dvd.setTitle("");
        }

        result.include("dvds", this.factory.getDvdDao().searchSimilarTitle(dvd.getTitle()));
        result.use(Results.page()).forward();
    }

	/**
	 * URL: 			/dvd
	 * View ok:			add.jsp
	 *
	 * The method adds a new dvd and updates the user.
	 */
	@Path("/dvd")
	@Post
	public void add(Dvd dvd, UploadedFile file) {
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

		result.include("dvd", dvd);
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
	 * Redirects to home if there are validation errors.
	 */
    private void redirectOnError() {
        validator.onErrorUse(Results.page()).forward("/WEB-INF/jsp/user/home.jsp");
    }

    /**
     * This method adds a dvd to a user's collection.
     */
    @Path("/dvd/addToList/{dvd.id}")
	public void addToMyList(Dvd dvd) {
	    User user = refreshUser();
	    validateAddToMyList(dvd, user);

		user.getDvds().add(dvd);
		factory.getUserDao().update(user);

		redirectToHome();
	}

    private void redirectToHome() {
        result.use(Results.logic()).redirectTo(UserController.class).home();
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

    private User refreshUser() {
        User user = userInfo.getUser();
		factory.getUserDao().refresh(user);
        return user;
    }

}
