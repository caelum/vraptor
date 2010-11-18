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

import java.util.Arrays;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.mydvds.controller.HomeController;
import br.com.caelum.vraptor.mydvds.dao.UserDao;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * Interceptor to check if the user is in the session.
 */
@Intercepts
public class AuthorizationInterceptor implements Interceptor {


	private final UserInfo info;
	private final UserDao dao;
	private final Result result;

	public AuthorizationInterceptor(UserInfo info, UserDao dao, Result result) {
		this.info = info;
		this.dao = dao;
		this.result = result;
	}

	/**
	 * the easiest way to implement the accepts method is creating an annotation
	 */
    public boolean accepts(ResourceMethod method) {
        return !method.containsAnnotation(Public.class);
    }

    /**
     * Intercepts the request and checks if there is a user logged in.
     */
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
    	/**
    	 * You can use the result even in interceptors.
    	 */
    	if (info.getUser() == null) {
    		// remember added parameters will survive one more request, when there is a redirect
    		result.include("errors", Arrays.asList(new ValidationMessage("user is not logged in", "user")));
    		result.redirectTo(HomeController.class).login();
    	} else {
	    	dao.refresh(info.getUser());
	    	// continues execution
	    	stack.next(method, resourceInstance);
    	}
    }

}
