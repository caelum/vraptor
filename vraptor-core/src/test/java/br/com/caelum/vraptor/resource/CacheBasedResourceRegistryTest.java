package br.com.caelum.vraptor.resource;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.VRaptorRequest;

public class CacheBasedResourceRegistryTest {

    private Mockery mockery;
    private CacheBasedResourceRegistry registry;
    private ResourceRegistry delegate;
    private ResourceMethod resource;
	private VRaptorRequest webRequest;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(ResourceRegistry.class);
        this.resource = mockery.mock(ResourceMethod.class);
        this.registry = new CacheBasedResourceRegistry(delegate);
        this.webRequest = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
        mockery.checking(new Expectations() {
            {
                one(delegate).gimmeThis("dog", "bark", webRequest); will(returnValue(resource));
            }
        });
    }

    @Test
    public void shouldUseTheProvidedResourceDuringFirstRequest() {
        ResourceMethod found = registry.gimmeThis("dog", "bark", webRequest);
        MatcherAssert.assertThat(found, Matchers.is(Matchers.equalTo(this.resource)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameResourceOnFurtherRequests() {
        ResourceMethod found = registry.gimmeThis("dog", "bark", webRequest);
        MatcherAssert.assertThat(registry.gimmeThis("dog", "bark", webRequest), Matchers.is(Matchers.equalTo(found)));
        mockery.assertIsSatisfied();
    }

}
