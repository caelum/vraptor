package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;

/**
 * Test class for uploading features with servlet 3.
 * 
 * @author Otávio Scherer Garcia
 */
public class Servlet3MultipartInterceptorTest {

    private Object instance;
    @Mock private InterceptorStack stack;
    @Mock private ResourceMethod method;
    @Mock private HttpServletRequest request;
    @Mock private MutableRequest parameters;
    @Mock private Validator validator;
    private Servlet3MultipartInterceptor interceptor;

    @Before
    public void setup()
        throws Exception {
        MockitoAnnotations.initMocks(this);
        interceptor = new Servlet3MultipartInterceptor(request, parameters, validator, new DefaultMultipartConfig());
    }

    @Test
    public void withFormURLEncoded()
        throws Exception {
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");

        assertThat(interceptor.accepts(method), equalTo(false));
    }

    @Test
    public void withMultipart()
        throws Exception {
        when(request.getContentType()).thenReturn("multipart/form-data");

        assertThat(interceptor.accepts(method), equalTo(true));
    }

    @Test
    public void withoutFiles()
        throws Exception {
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParts()).thenReturn(new ArrayList<Part>());

        interceptor.intercept(stack, method, instance);

        verify(parameters, never()).setParameter(any(String.class), anyString());
    }

    @Test
    public void withOnlyFields()
        throws Exception {
        Part field0 = new VraptorPart("myField0", "the value");
        Part field1 = new VraptorPart("myField1", "other value");

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParts()).thenReturn(Arrays.asList(field0, field1));

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(2)).setParameter(anyString(), anyString());
    }

    @Test
    public void withFieldsAndFiles()
        throws Exception {
        Part field0 = new VraptorPart("myField0", "the value");
        Part field1 = new VraptorPart("myField1", "other value");
        Part file0 = new VraptorPart("myFile0", "text/plain", "file.txt", "vraptor3".getBytes());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParts()).thenReturn(Arrays.asList(field0, field1, file0));

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(3)).setParameter(anyString(), anyString());
    }

    @Test
    public void withCompleteFilePath()
        throws Exception {
        Part file0 = new VraptorPart("myFile0", "text/plain", "c:\\a\\windows\\path\\file.txt", "vraptor3".getBytes());
        Part file1 = new VraptorPart("myFile0", "text/plain", "/a/unix/path/file.txt", "vraptor3".getBytes());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParts()).thenReturn(Arrays.asList(file0,file1));

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(2)).setParameter(anyString(), anyString());
    }

    @Test
    public void exceedsSizeLimit()
        throws Exception {
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParts()).thenThrow(new IllegalStateException());

        interceptor.intercept(stack, method, instance);

        verify(validator).add(any(Message.class));
    }
}
