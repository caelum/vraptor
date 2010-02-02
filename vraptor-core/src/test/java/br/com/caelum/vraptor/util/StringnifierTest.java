package br.com.caelum.vraptor.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;



public class StringnifierTest {

	static class X {
		public void method(List<X> l) {

		}
	}

	@Test
	public void returnSimpleNameOfMethodWithoutArguments() throws SecurityException, NoSuchMethodException {
		Method m = Object.class.getMethod("toString");
		Assert.assertEquals("Object.toString()", Stringnifier.simpleNameFor(m));
	}

	@Test
	public void returnSimpleNameOConstructorWithoutArguments() throws SecurityException, NoSuchMethodException {
		Constructor<Object> c = Object.class.getConstructor();
		Assert.assertEquals("Object()", Stringnifier.simpleNameFor(c));
	}

	@Test
	public void returnSimpleNameOfMethodWithArguments() throws SecurityException, NoSuchMethodException {
		Method m = Comparator.class.getMethod("compare", Object.class, Object.class);
		Assert.assertEquals("Comparator.compare(Object, Object)", Stringnifier.simpleNameFor(m));
	}

	@Test
	public void returnSimpleNameOfMethodWithArgumentsGenerics() throws SecurityException, NoSuchMethodException {
		Method m = X.class.getMethod("method", List.class);
		Assert.assertEquals("X.method(List)", Stringnifier.simpleNameFor(m));
	}

}
