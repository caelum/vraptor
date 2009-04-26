package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.Resource;

public class VRaptor2MethodLookupTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    class NonVRaptorComponent {
        public void name() {
        }
    }

    @Test
    public void shouldUseVRaptor3AlgorithmIfNotAVRaptor2Component() {
        final Resource resource = mockery.resource(NonVRaptorComponent.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("id", "name"), is(equalTo(new DefaultResourceAndMethodLookup(resource).methodFor(
                "id", "name"))));
        mockery.assertIsSatisfied();
    }

    @Component
    static class MyResource {
        public static void ignorableStatic() {
        }

        protected void ignorableProtected() {
        }

        @Path("/findable")
        public void findable() {
        }
    }

    @Test
    public void ignoresNonPublicMethod() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.ignorableStatic.logic", "ignorableStatic"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresStaticMethod() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.ignorableProtected.logic", "ignorableProtected"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfNothingFound() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.unfindable.logic", "unfindable"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsTheCorrectDefaultResourceMethodIfFound() throws SecurityException, NoSuchMethodException {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.findable.logic", "findable"), is(VRaptorMatchers.resourceMethod(MyResource.class.getMethod("findable"))));
        mockery.assertIsSatisfied();
    }

}
