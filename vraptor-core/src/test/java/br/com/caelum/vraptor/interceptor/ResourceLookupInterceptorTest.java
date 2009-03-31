package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ResourceLookupInterceptorTest {

    private Mockery mockery;
    private UrlToResourceTranslator translator;
    private VRaptorRequest request;
    private ResourceLookupInterceptor lookup;
    private HttpServletRequest webRequest;
    private HttpServletResponse webResponse;
    private Container container;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.translator = mockery.mock(UrlToResourceTranslator.class);
        this.webRequest = mockery.mock(HttpServletRequest.class);
        this.webResponse = mockery.mock(HttpServletResponse.class);
        this.request = new VRaptorRequest(null, webRequest, webResponse);
        this.container = mockery.mock(Container.class);
        this.lookup = new ResourceLookupInterceptor(translator, request, container);
    }

    @Test
    public void shouldHandle404() throws IOException, InterceptionException {
        final StringWriter writer = new StringWriter();
        mockery.checking(new Expectations() {
            {
                one(translator).translate(webRequest);
                will(returnValue(null));
                one(webResponse).setStatus(404);
                one(webResponse).getWriter();
                will(returnValue(new PrintWriter(writer)));
            }
        });
        lookup.intercept(null, null, null);
        MatcherAssert.assertThat(writer.getBuffer().toString(), Matchers.is(Matchers.equalTo("resource not found\n")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseResourceMethodFoundWithNextInterceptor() throws IOException, InterceptionException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        mockery.checking(new Expectations() {
            {
                one(translator).translate(webRequest);
                will(returnValue(method));
                one(stack).next(method, null);
                one(container).register(method);
            }
        });
        lookup.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

}
