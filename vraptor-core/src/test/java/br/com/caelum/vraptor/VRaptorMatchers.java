package br.com.caelum.vraptor;

import java.util.Collection;
import java.util.Collections;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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
}
