package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.DogController;

public class VRaptor2PathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private Resource resource;
    private VRaptor2PathResolver resolver;
    private Config config;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.resource = mockery.mock(Resource.class);
        this.config = mockery.mock(Config.class);
        mockery.checking(new Expectations() {
            {
                one(config).getViewPattern(); will(returnValue("/$component/$logic.$result.jsp"));
            }
        });
        this.resolver = new VRaptor2PathResolver(config);
    }

    @Test
    public void shouldDelegateToVraptor3IfItsNotAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                exactly(2).of(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(DogController.class.getDeclaredMethod("bark")));
                exactly(2).of(resource).getType();
                will(returnValue(DogController.class));
            }
        });
        String result = resolver.pathFor(method, "ok");
        assertThat(result, is(equalTo("/DogController/bark.ok.jsp")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseVRaptor2AlgorithmIfAVRaptor2Component() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                one(method).getResource();
                will(returnValue(resource));
                one(method).getMethod();
                will(returnValue(CowLogic.class.getDeclaredMethod("eat")));
                exactly(2).of(resource).getType();
                will(returnValue(CowLogic.class));
            }
        });
        String result = resolver.pathFor(method, "ok");
        assertThat(result, is(equalTo("/cow/eat.ok.jsp")));
        mockery.assertIsSatisfied();
    }

}
