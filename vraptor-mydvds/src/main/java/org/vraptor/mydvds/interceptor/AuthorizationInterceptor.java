package org.vraptor.mydvds.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.vraptor.mydvds.logic.LoginController;
import org.vraptor.mydvds.logic.UserController;
import org.vraptor.mydvds.model.User;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Interceptor to check if the user is in the session.
 */
@Intercepts
public class AuthorizationInterceptor implements Interceptor {


	private final HttpServletResponse response;
    private final HttpSession session;

	public AuthorizationInterceptor(HttpSession session, HttpServletResponse response) {
		this.session = session;
        this.response = response;
	}

    public boolean accepts(ResourceMethod method) {
        return notLogin(method) && notNewUser(method);
    }

    private boolean notNewUser(ResourceMethod method) {
        Method invokedMethod = method.getMethod();
        if (invokedMethod.getDeclaringClass().equals(UserController.class)) {
            return !"add".equals(invokedMethod.getName()) && !"userAdded".equals(invokedMethod.getName());
        }
        return true;
    }

    private boolean notLogin(ResourceMethod method) {
        return !method.getMethod().getDeclaringClass().equals(LoginController.class);
    }

    /**
     * Intercepts the request and checks if there is a user logged in.
     */
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        /*
         * Gets the user in the current session.
         */
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            this.response.setHeader("SessionTimeOut", "The Session has expired");
            throw new InterceptionException(new AuthenticationException("The Session has expired"));
        } else {
            // continues execution
            stack.next(method, resourceInstance);
        }
    }

}
