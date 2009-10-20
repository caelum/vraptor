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
package br.com.caelum.vraptor.mydvds.interceptor;

import static br.com.caelum.vraptor.view.Results.logic;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.mydvds.controller.HomeController;
import br.com.caelum.vraptor.mydvds.controller.UsersController;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Validations;

/**
 * Interceptor to check if the user is in the session.
 */
@Intercepts
public class AuthorizationInterceptor implements Interceptor {


	private final UserInfo info;
	private final UserDao dao;
	private final Validator validator;

	public AuthorizationInterceptor(UserInfo info, UserDao dao, Validator validator) {
		this.info = info;
		this.dao = dao;
		this.validator = validator;
	}

    public boolean accepts(ResourceMethod method) {
        return notLogin(method) && notNewUser(method);
    }

    private boolean notNewUser(ResourceMethod method) {
        Method invokedMethod = method.getMethod();
        if (invokedMethod.getDeclaringClass().equals(UsersController.class)) {
            return !"add".equals(invokedMethod.getName()) && !"userAdded".equals(invokedMethod.getName());
        }
        return true;
    }

    private boolean notLogin(ResourceMethod method) {
        return !method.getMethod().getDeclaringClass().equals(HomeController.class);
    }

    /**
     * Intercepts the request and checks if there is a user logged in.
     */
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
    	/**
    	 * You can use the validator even in interceptors.
    	 */
    	validator.checking(new Validations() {{
    		that(info.getUser() != null, "user", "user.is.not.logged.in");
    	}});
    	validator.onErrorUse(logic()).redirectTo(HomeController.class).index();
    	dao.refresh(info.getUser());
    	// continues execution
    	stack.next(method, resourceInstance);
    }

}
