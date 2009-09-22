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

	class SuperClassThatImplementsCF implements ComponentFactory<String> {
		public String getInstance() {
			return "abc";
		}
	}

	class ClassThatImplementsCFExtending extends SuperClassThatImplementsCF {
	}

	@Test
	public void shoudWorkWithSubclassesOfComponenetFactoryImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatImplementsCFExtending.class));
		Assert.assertEquals(String.class, c);
	}

	@Test
	public void shoudWorkWithImplementationsOfComponenetFactorySubinterfacesImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatImplementsCFIndirectly.class));
		Assert.assertEquals(String.class, c);
	}

	interface InterfaceThatExtendCF extends ComponentFactory<String> {
	}

	class ClassThatImplementsCFIndirectly implements InterfaceThatExtendCF {
		public String getInstance() {
			return "def";
		}
	}

	class ClassThatIsNotCFAtAll {
		public String getInstance() {
			return "def";
		}
	}

	@Test(expected = ComponentRegistrationException.class)
	public void shoudNotWorkWithClassesThatDoesNotImplementComponentFactory() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatIsNotCFAtAll.class));
	}
}
