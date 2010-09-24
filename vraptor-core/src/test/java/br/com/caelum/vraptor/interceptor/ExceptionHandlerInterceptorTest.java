package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.ExceptionMapper;
import br.com.caelum.vraptor.core.ExceptionRecorder;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ExceptionHandlerInterceptorTest {

    private Object instance;
    @Mock private InterceptorStack stack;
    @Mock private ResourceMethod method;
    @Mock private ExceptionMapper mapper;
    @Mock private Result result;
    @Mock private HttpServletRequest request;
    @Mock private ExceptionRecorder<Result> mockRecorder;
    private ExceptionHandlerInterceptor interceptor;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        interceptor = new ExceptionHandlerInterceptor(mapper, result, request);
    }

    /**
     * Test when the exception is found.
     */
    @Test
    public void withRootException() {
        final Exception e = new IllegalStateException();
        when(mapper.findByException(e)).thenReturn(mockRecorder);
        doThrow(new InterceptionException(e)).when(stack).next(method, instance);

        interceptor.intercept(stack, method, instance);
        verify(mockRecorder).replay(result);
    }

    /**
     * Test when the exception is not found, so vraptor needs only rethrows the
     * exception.
     */
    @Test
    public void whenNotFoundException() {
        final Exception e = new IllegalArgumentException();
        when(mapper.findByException(e)).thenReturn(null);
        doThrow(new InterceptionException(e)).when(stack).next(method, instance);

        try {
        	interceptor.intercept(stack, method, instance);
        	Assert.fail("Should throw InterceptionException");
        } catch (InterceptionException e2) {
        	assertEquals(e2.getCause(), e);
		}
    }
}
