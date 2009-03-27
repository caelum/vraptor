package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultResultTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.container = mockery.mock(Container.class);
    }

    public static class MyView implements View {

    }

    @Test
    public void shouldUseContainerForNewView() {
        DefaultResult result = new DefaultResult(request, container);
        final MyView expectedView = new MyView();
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyView.class);
                will(returnValue(expectedView));
            }
        });
        MyView view = result.use(MyView.class);
        MatcherAssert.assertThat(view, Matchers.is(Matchers.equalTo(expectedView)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldSetRequestAttribute() {
        DefaultResult result = new DefaultResult(request, container);
        mockery.checking(new Expectations() {
            {
                one(request).setAttribute("my_key", "my_value");
            }
        });
        result.include("my_key", "my_value");
        mockery.assertIsSatisfied();
    }

}
