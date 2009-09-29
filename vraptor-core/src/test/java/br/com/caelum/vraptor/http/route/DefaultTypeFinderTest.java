package br.com.caelum.vraptor.http.route;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;

public class DefaultTypeFinderTest {


	public static class AController {
		public void aMethod(Bean bean, String path) {

		}
		public void otherMethod(BeanExtended extended) {

		}
	}

	public static class Bean {

		public Bean2 getBean2() {
			return new Bean2();
		}
	}

	public static class BeanExtended extends Bean2 {

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

		final Method method = new Mirror().on(AController.class).reflect().method("aMethod").withArgs(Bean.class, String.class);
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
	@Test
	public void shouldGetTypesCorrectlyOnInheritance() throws Exception {
		Mockery mockery = new Mockery();
		final ParameterNameProvider provider = mockery.mock(ParameterNameProvider.class);

		final Method method = new Mirror().on(AController.class).reflect().method("otherMethod").withArgs(BeanExtended.class);
		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(method);
				will(returnValue(new String[] {"extended"}));
			}
		});
		DefaultTypeFinder finder = new DefaultTypeFinder(provider);
		Map<String, Class<?>> types = finder.getParameterTypes(method, new String[] {"extended.id"});

		assertEquals(Integer.class, types.get("extended.id"));
	}
}
