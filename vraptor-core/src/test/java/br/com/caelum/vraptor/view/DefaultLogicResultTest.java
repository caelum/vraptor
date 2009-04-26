package br.com.caelum.vraptor.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private MyComponent instance;
    private Container container;
    private MethodLookupBuilder builder;
    private HttpServletResponse response;

    public static class MyComponent {
        public void base() {
        }
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.instance = new MyComponent();
        this.container = mockery.mock(Container.class);
        this.builder = mockery.mock(MethodLookupBuilder.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.logicResult = new DefaultLogicResult(container, builder, response);
    }

    @Test
    public void instantiatesUsingTheContainer() {
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyComponent.class); will(returnValue(instance));
            }
        });
        MyComponent component = logicResult.redirectServerTo(MyComponent.class);
        assertThat(component, is(equalTo(instance)));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {
        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(builder).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(response).sendRedirect(url);
            }
        });
        logicResult.redirectClientTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }

}
