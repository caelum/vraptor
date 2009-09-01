package br.com.caelum.vraptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import br.com.caelum.vraptor.http.route.Route;

public class VRaptorMatchers {
	public static Matcher<Collection<?>> hasOneCopyOf(final Object item) {
		return new TypeSafeMatcher<Collection<?>>(){

			public void describeTo(Description description) {
				description.appendText("a collection containing one copy of").appendValue(item);
			}

			@Override
			protected void describeMismatchSafely(Collection<?> sut, Description mismatchDescription) {
				mismatchDescription
					.appendText("a collection containing")
					.appendValue(Collections.frequency(sut, item))
					.appendText("copies");
			}

			@Override
			protected boolean matchesSafely(Collection<?> sut) {
				return Collections.frequency(sut, item) == 1;
			}

		};
	}

	public static Matcher<Route> canHandle(final Class<?> type, final Method method) {
		return new TypeSafeMatcher<Route>() {

			@Override
			protected void describeMismatchSafely(Route item, Description mismatchDescription) {
				mismatchDescription.appendValue(item);
			}

			@Override
			protected boolean matchesSafely(Route item) {
				return item.canHandle(type, method);
			}

			public void describeTo(Description description) {
				description.appendText("a route that can handle class ")
						.appendValue(type).appendText(" method ").appendValue(method);
			}
		};
	}
}
