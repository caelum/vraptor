package br.com.caelum.vraptor.validator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class ShouldBe<T> extends BaseMatcher<T> {
	private final Matcher<T> matcher;

	public ShouldBe(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	public boolean matches(Object arg) {
		return matcher.matches(arg);
	}

	public void describeTo(Description description) {
		description.appendText("should be ").appendDescriptionOf(matcher);
	}

	@Override
	public void describeMismatch(Object item, Description mismatchDescription) {
		// TODO(ngd): unit tests....
		matcher.describeMismatch(item, mismatchDescription);
	}

	@Factory
	public static <T> Matcher<T> shouldBe(Matcher<T> matcher) {
		return new ShouldBe<T>(matcher);
	}

	@Factory
	public static <T> Matcher<? super T> shouldBe(T value) {
		return shouldBe(equalTo(value));
	}

	@Factory
	public static <T> Matcher<? super T> shouldBe(Class<T> type) {
		return shouldBe(instanceOf(type));
	}
}
