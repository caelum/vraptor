package br.com.caelum.vraptor.resource;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class CacheBasedResourceRegistryTest {

    private Mockery mockery;
    private CacheBasedResourceRegistry registry;
    private ResourceRegistry delegate;
    private ResourceMethod resource;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(ResourceRegistry.class);
        this.resource = mockery.mock(ResourceMethod.class);
        this.registry = new CacheBasedResourceRegistry(delegate);
        mockery.checking(new Expectations() {
            {
                one(delegate).gimmeThis("dog", "bark"); will(returnValue(resource));
            }
        });
    }

    @Test
    public void shouldUseTheProvidedResourceDuringFirstRequest() {
        ResourceMethod found = registry.gimmeThis("dog", "bark");
        MatcherAssert.assertThat(found, Matchers.is(Matchers.equalTo(this.resource)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameResourceOnFurtherRequests() {
        ResourceMethod found = registry.gimmeThis("dog", "bark");
        MatcherAssert.assertThat(registry.gimmeThis("dog", "bark"), Matchers.is(Matchers.equalTo(found)));
        mockery.assertIsSatisfied();
    }

}
