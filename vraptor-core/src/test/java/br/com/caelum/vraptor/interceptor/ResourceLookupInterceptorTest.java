package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ResourceLookupInterceptorTest {

    private Mockery mockery;
    private UrlToResourceTranslator translator;
    private VRaptorRequest request;
    private ResourceLookupInterceptor lookup;
    private HttpServletRequest webRequest;
    private HttpServletResponse webResponse;
    private RequestInfo requestInfo;
	private ResourceNotFoundHandler notFoundHandler;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.translator = mockery.mock(UrlToResourceTranslator.class);
        this.webRequest = mockery.mock(HttpServletRequest.class);
        this.webResponse = mockery.mock(HttpServletResponse.class);
        this.request = new VRaptorRequest(null, webRequest, webResponse);
        this.requestInfo = mockery.mock(RequestInfo.class);
        this.notFoundHandler = mockery.mock(ResourceNotFoundHandler.class);
        this.lookup = new ResourceLookupInterceptor(translator, requestInfo, notFoundHandler, request);
    }

    @Test
    public void shouldHandle404() throws IOException, InterceptionException {
        mockery.checking(new Expectations() {
            {
                one(translator).translate(webRequest);
                will(returnValue(null));
                one(notFoundHandler).couldntFind(webResponse);
            }
        });
        lookup.intercept(null, null, null);
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
                one(requestInfo).setResourceMethod(method);
            }
        });
        lookup.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

}
