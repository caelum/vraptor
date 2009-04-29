package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

public class ResourceLookupInterceptorTest {

    private Mockery mockery;
    private UrlToResourceTranslator translator;
    private RequestInfo request;
    private ResourceLookupInterceptor lookup;
    private HttpServletRequest webRequest;
    private HttpServletResponse webResponse;
    private MethodInfo requestInfo;
	private ResourceNotFoundHandler notFoundHandler;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.translator = mockery.mock(UrlToResourceTranslator.class);
        this.webRequest = mockery.mock(HttpServletRequest.class);
        this.webResponse = mockery.mock(HttpServletResponse.class);
        this.request = new RequestInfo(null, webRequest, webResponse);
        this.requestInfo = mockery.mock(MethodInfo.class);
        this.notFoundHandler = mockery.mock(ResourceNotFoundHandler.class);
        this.lookup = new ResourceLookupInterceptor(translator, requestInfo, notFoundHandler, request);
    }

    @Test
    public void shouldHandle404() throws IOException, InterceptionException {
        mockery.checking(new Expectations() {
            {
                one(translator).translate(webRequest);
                will(returnValue(null));
                one(notFoundHandler).couldntFind(request);
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
