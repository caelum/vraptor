package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.VRaptorInfo;

public class DefaultResourceRegistryTest {

    private Mockery mockery;
    private DefaultResourceRegistry registry;
    private MethodLookupBuilder builder;
    private ResourceAndMethodLookup methodLookup;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.builder = mockery.mock(MethodLookupBuilder.class);
        this.methodLookup = mockery.mock(ResourceAndMethodLookup.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(with(VRaptorMatchers.resource(VRaptorInfo.class)));
                will(returnValue(methodLookup));
            }
        });
        this.registry = new DefaultResourceRegistry(builder);
    }

    @Test
    public void testReturnsResourceIfFound() throws SecurityException, NoSuchMethodException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final Resource resource = mockery.mock(Resource.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(resource);
                will(returnValue(methodLookup));
                one(methodLookup).methodFor("/clients", "POST");
                will(returnValue(method));
            }
        });
        registry.register(Arrays.asList(resource));
        assertThat(registry.gimmeThis("/clients", "POST"), is(equalTo(method)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testReturnsNullIfResourceNotFound() {
        mockery.checking(new Expectations() {
            {
                one(methodLookup).methodFor("unknown_id", "POST");
                will(returnValue(null));
            }
        });
        ResourceMethod method = registry.gimmeThis("unknown_id", "POST");
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterVRaptorInfoByDefault() throws SecurityException, NoSuchMethodException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        mockery.checking(new Expectations() {
            {
                one(methodLookup).methodFor("/is_using_vraptor", "GET");
                will(returnValue(method));
            }
        });
        assertThat(registry.gimmeThis("/is_using_vraptor", "GET"), is(equalTo(method)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAddAllResourcesToACommonList() {
        final Resource myResource = mockery.mock(Resource.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(myResource);
                will(returnValue(null));
            }
        });
        registry.register(Arrays.asList(myResource));
        assertThat(registry.all(), Matchers.hasItem(myResource));
        mockery.assertIsSatisfied();
    }

}
