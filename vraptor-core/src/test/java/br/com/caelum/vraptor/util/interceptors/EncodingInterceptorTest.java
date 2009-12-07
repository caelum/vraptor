package br.com.caelum.vraptor.util.interceptors;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import static org.mockito.Mockito.*;

public class EncodingInterceptorTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private InterceptorStack stack;
    @Mock private ResourceMethod method;
    private Object resourceInstance;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSetEncodingToUTF8() throws Exception {
        EncodingInterceptor interceptor = new EncodingInterceptor(request, response);

        interceptor.intercept(stack, method, resourceInstance);

        InOrder callOrder = inOrder(request, response, stack);
        callOrder.verify(request).setCharacterEncoding("UTF-8");
        callOrder.verify(response).setCharacterEncoding("UTF-8");
        callOrder.verify(stack).next(method, resourceInstance);
    }

    @Test(expected=InterceptionException.class)
    public void shouldThrowExceptionWhenAnUnsupportedEncodingExceptionOccurs() throws Exception {
        EncodingInterceptor interceptor = new EncodingInterceptor(request, response);

        doThrow(new UnsupportedEncodingException()).when(request).setCharacterEncoding(anyString());

        interceptor.intercept(stack, method, resourceInstance);
    }

}
