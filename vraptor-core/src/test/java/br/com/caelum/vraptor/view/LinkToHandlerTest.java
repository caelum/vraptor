package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.http.route.Router;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletContext;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class LinkToHandlerTest {
    private @Mock ServletContext context;
    private @Mock Router router;
    private LinkToHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.handler = new LinkToHandler(context, router);
        when(context.getContextPath()).thenReturn("/path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvocationIsIncomplete() {
        //${linkTo[TestController]}
        handler.get(TestController.class).toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvokingInexistantMethod() {
        //${linkTo[TestController].nonExists}
        handler.get(TestController.class).get("nonExists").toString();
    }
    @Test
    public void shouldReturnWantedUrlWithoutArgs() {
        when(router.urlFor(TestController.class, TestController.class.getDeclaredMethods()[0], new Object[2])).thenReturn("/expectedURL");

        //${linkTo[TestController].method}
        String uri = handler.get(TestController.class).get("method").toString();
        assertThat(uri, is("/path/expectedURL"));
    }

    @Test
    public void shouldReturnWantedUrlWithParamArgs() {
        String a = "test";
        int b = 3;
        when(router.urlFor(TestController.class, TestController.class.getDeclaredMethods()[0], new Object[]{a, b})).thenReturn("/expectedURL");
        //${linkTo[TestController].method['test'][3]}
        String uri = handler.get(TestController.class).get("method").get(a).get(b).toString();
        assertThat(uri, is("/path/expectedURL"));
    }

    static class TestController {
        void method(String a, int b) {

        }
    }
}
