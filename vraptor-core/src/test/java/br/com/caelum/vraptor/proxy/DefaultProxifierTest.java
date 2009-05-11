package br.com.caelum.vraptor.proxy;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author Fabio Kung
 */
public class DefaultProxifierTest {

    @Test
    public void shouldProxifyInterfaces() {
        Proxifier proxifier = new DefaultProxifier();
        TheInterface proxy = proxifier.proxify(TheInterface.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithDefaultConstructors() {
        Proxifier proxifier = new DefaultProxifier();
        TheClass proxy = proxifier.proxify(TheClass.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return true;
            }
        });
        assertTrue(proxy.wasCalled());
    }

    @Test
    public void shouldProxifyConcreteClassesWithComplexConstructorsAndPassNullForAllParameters() {
        Proxifier proxifier = new DefaultProxifier();
        TheClassWithComplexConstructor proxy = proxifier.proxify(TheClassWithComplexConstructor.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertThat(proxy.getFirstDependency(), is(nullValue()));
        assertThat(proxy.getSecondDependency(), is(nullValue()));
    }

    @Test
    public void shouldTryAllConstructorsInDeclarationOrder() {
        Proxifier proxifier = new DefaultProxifier();
        TheClassWithManyConstructors proxy = proxifier.proxify(TheClassWithManyConstructors.class, new MethodInvocation() {
            public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
                return superMethod.invoke(proxy, args);
            }
        });
        assertTrue(proxy.wasNumberConstructorCalled());
        assertThat(proxy.getNumber(), is(nullValue()));
    }

}
