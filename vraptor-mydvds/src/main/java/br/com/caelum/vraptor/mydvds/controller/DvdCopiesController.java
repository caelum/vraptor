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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.mydvds.dao.DvdDao;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.mydvds.interceptor.UserInfo;
import br.com.caelum.vraptor.mydvds.model.Dvd;
import br.com.caelum.vraptor.mydvds.model.DvdCopy;
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
public class DvdCopiesController {

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
	public DvdCopiesController(DvdDao dao, UserDao userDao, UserInfo userInfo, Result result, Validator validator) {
		this.dao = dao;
		this.userDao = userDao;
		this.result = result;
        this.validator = validator;
        this.userInfo = userInfo;
	}


    /**
     * Accepts HTTP PUT requests.
     * URL:  /users/login/dvds/id (for example, /users/john/dvds/3 adds the dvd with id 3 to the john's collection)
     * View: redirects to user's home
     *
     * You can use more than one variable on URI. Since the browsers don't support PUT method
     * you have to pass an additional parameter: _method=PUT for calling this method.
     *
     * This method adds a dvd to a user's collection.
     */
    @Path("/users/{user.login}/dvds/{dvd.id}")
    @Put
	public void addToMyList(final User user, final Dvd dvd) {
	    final User sessionUser = refreshUser();
	    validator.checking(new Validations() {{
	    	that(user.getLogin(), is(sessionUser.getLogin()),"user", "you_cant_add_to_others_list");
		    that(sessionUser.getDvds(), not(hasItem(dvd)), "dvd", "you_already_have_this_dvd");
		}});

		validator.onErrorUse(Results.page()).of(UsersController.class).home();

		dao.add(new DvdCopy(user, dvd));

		result.use(Results.logic()).redirectTo(UsersController.class).home();
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
