package br.com.caelum.vraptor.http.route;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;

public class DefaultTypeFinderTest {


	public static class AController {
		public void aMethod(Bean bean, String path) {

		}
	}

	public static class Bean {

		public Bean2 getBean2() {
			return new Bean2();
		}
	}

	public static class Bean2 {
		public Integer getId() {
			return 1;
		}
	}
	@Test
	public void shouldGetTypesCorrectly() throws Exception {
		Mockery mockery = new Mockery();
		final ParameterNameProvider provider = mockery.mock(ParameterNameProvider.class);

		final Method method = AController.class.getDeclaredMethods()[0];
		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(method);
				will(returnValue(new String[] {"bean", "path"}));
			}
		});
		DefaultTypeFinder finder = new DefaultTypeFinder(provider);
		Map<String, Class<?>> types = finder.getParameterTypes(method, new String[] {"bean.bean2.id", "path"});

		assertEquals(Integer.class, types.get("bean.bean2.id"));
		assertEquals(String.class, types.get("path"));
	}
}
