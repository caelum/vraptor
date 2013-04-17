package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.route.Router;

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
    
    @Test
    public void shouldReturnWantedUrlWithPartialParamArgs() {
    	String a = "test";
    	when(router.urlFor(TestController.class, TestController.class.getDeclaredMethods()[2], new Object[]{a, null})).thenReturn("/expectedUrl");
    	//${linkTo[TestController].anotherMethod['test']}
    	String uri = handler.get(TestController.class).get("anotherMethod").get(a).toString();
    	assertThat(uri, is("/path/expectedUrl"));
    }
    
    @Test
    public void shouldReturnWantedUrlForOverrideMethodWithParamArgs() throws NoSuchMethodException, SecurityException {
    	String a = "test";
    	when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("method", new Class[]{String.class}), new Object[]{a})).thenReturn("/expectedURL");
    	//${linkTo[TestSubGenericController].method['test']}]
    	String uri = handler.get(SubGenericController.class).get("method").get(a).toString();
    	assertThat(uri, is("/path/expectedURL"));
    }
    
    @Test
    public void shouldReturnWantedUrlForOverrideMethodWithParialParamArgs() throws SecurityException, NoSuchMethodException {
    	String a = "test";
    	when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("anotherMethod", new Class[]{String.class, String.class}), new Object[]{a, null})).thenReturn("/expectedURL");
    	//${linkTo[TestSubGenericController].anotherMethod['test']}]
    	String uri = handler.get(SubGenericController.class).get("anotherMethod").get(a).toString();
    	assertThat(uri, is("/path/expectedURL"));
    }
    
    @Test
    public void shouldReturnWantedUrlForMethodsWithSameName() {
    	String a = "test";
    	when(router.urlFor(TestController.class, TestController.class.getDeclaredMethods()[1], a)).thenReturn("/expectedUrl");
    	//${linkTo[TestController].method['test']}
    	String uri = handler.get(TestController.class).get("method").get(a).toString();
    	assertThat(uri, is("/path/expectedUrl"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPassingMoreArgsThanMethodSupports() {
    	String a = "test";
        int b = 3;
        String c = "anotherTest";
        //${linkTo[TestController].anotherMethod['test'][3]['anotherTest']}
        handler.get(TestController.class).get("anotherMethod").get(a).get(b).get(c).toString();
    }
    
    static class TestController {
        void method(String a, int b) {

        }
        void method(String a) {
        	
        }
        void anotherMethod(String a, int b) {
        	
        }
    }
}
