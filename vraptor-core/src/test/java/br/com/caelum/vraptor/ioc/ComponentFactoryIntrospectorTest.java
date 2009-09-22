package br.com.caelum.vraptor.ioc;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class ComponentFactoryIntrospectorTest {
	public static class MyFactory implements ComponentFactory<NeedsCustomInstantiation> {
		public NeedsCustomInstantiation getInstance() {
			return null;
		}
	}

	@Test
	public void shouldExtractTargetTypeFromGenericDefinition() {
		Object targetType = new ComponentFactoryIntrospector().targetTypeForComponentFactory(MyFactory.class);
		assertEquals(NeedsCustomInstantiation.class, targetType);
	}

	public static class FactoryWithoutTargetType implements ComponentFactory {
		public Object getInstance() {
			return null;
		}
	}

	@Test(expected = ComponentRegistrationException.class)
	public void shouldRequireGenericTypeInformationToBePresent() {
		new ComponentFactoryIntrospector().targetTypeForComponentFactory(FactoryWithoutTargetType.class);
	}

	class XX implements ComponentFactory<String> {
		public String getInstance() {
			return "abc";
		}
	}

	class XX2 extends XX {
	}

	@Test
	public void shoudWorkWithSubclassesOfComponenetFactoryImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((XX2.class));
		Assert.assertEquals(String.class, c);
	}

	@Test
	public void shoudWorkWithImplementationsOfComponenetFactorySubinterfacesImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((YY2.class));
		Assert.assertEquals(String.class, c);
	}

	interface YY extends ComponentFactory<String> {
	}

	class YY2 implements YY {
		public String getInstance() {
			return "def";
		}
	}

	class ZZ {
		public String getInstance() {
			return "def";
		}
	}

	@Test(expected = ComponentRegistrationException.class)
	public void shoudNotWorkWithClassesThatDoesNotImplementComponentFactory() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ZZ.class));
	}
}
