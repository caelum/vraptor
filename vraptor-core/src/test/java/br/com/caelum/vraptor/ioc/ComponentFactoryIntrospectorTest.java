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

	interface YY extends ComponentFactory<String> {
	}

	class YY2 implements YY {
		public String getInstance() {
			return "def";
		}
	}

	@Test
	public void shoudRegisterSubclassesOfComponenetFactoryImplementations() {
		// problema no metodo targetTypeForComponentFactory, ver no
		// componentfactorybean!
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((XX2.class));
		Assert.assertEquals(String.class, c);
	}
}
