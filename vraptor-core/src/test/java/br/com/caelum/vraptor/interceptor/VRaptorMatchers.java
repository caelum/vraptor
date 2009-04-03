package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Useful matchers to use while mocking and hamcresting tests with internal
 * vraptor information.
 * 
 * @author Guilherme Silveira
 */
public class VRaptorMatchers {

    public static Matcher<ResourceMethod> resourceMethod(final Method method) {
        return new BaseMatcher<ResourceMethod>() {

            public boolean matches(Object item) {
                if (!(item instanceof ResourceMethod)) {
                    return false;
                }
                ResourceMethod other = (ResourceMethod) item;
                return other.getMethod().equals(method);
            }

            public void describeTo(Description description) {
                description.appendText(" resource method for " + method.getName());
            }

        };
    }

}
