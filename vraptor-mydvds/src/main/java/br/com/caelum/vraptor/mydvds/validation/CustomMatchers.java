package br.com.caelum.vraptor.mydvds.validation;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This class creates custom Hamcrest matchers for making validations more readable.
 * @author Lucas Cavalcanti
 *
 */
public class CustomMatchers {

    private static TypeSafeMatcher<String> EMPTY = new TypeSafeMatcher<String>() {

        @Override
        protected void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText(" " + item);
        }

        @Override
        protected boolean matchesSafely(String item) {
            return item != null && !item.equals("");
        }

        public void describeTo(Description description) {
            description.appendText(" not empty");
        }

    };

    /**
     * matches if the given string is not empty. This matcher is null-safe.
     * @return
     */
    public static TypeSafeMatcher<String> notEmpty() {
        return EMPTY;
    }

}
