package br.com.caelum.vraptor.ioc;

import static org.junit.Assert.assertEquals;

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
}
