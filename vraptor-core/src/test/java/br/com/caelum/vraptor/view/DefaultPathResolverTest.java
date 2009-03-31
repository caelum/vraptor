package br.com.caelum.vraptor.view;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.DogController;

public class DefaultPathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private Resource resource;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.resource = mockery.mock(Resource.class);
    }
    
    @Test
    public void shouldUseResourceTypeAndMethodNameToResolveJsp() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver();
        String result = resolver.pathFor(method, "ok");
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/DogController/bark.ok.jsp")));
        mockery.assertIsSatisfied();
    }
}
