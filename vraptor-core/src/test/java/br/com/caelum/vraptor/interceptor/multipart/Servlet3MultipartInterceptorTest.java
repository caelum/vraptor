package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        interceptor = new Servlet3MultipartInterceptor(request, parameters, validator);
    }

    @Test
    public void shouldNotAcceptFormURLEncoded()
        throws Exception {
        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(false));
    }

    @Test
    public void shouldAcceptMultipart()
        throws Exception {
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(true));
    }

    @Test
    public void withFieldsOnly()
        throws Exception {
        Part field0 = new VraptorPart("myField0", "the value");
        Part field1 = new VraptorPart("myField1", "other value");

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParts()).thenReturn(Arrays.asList(field0, field1));

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("myField0", "the value");
        verify(parameters).setParameter("myField1", "other value");
    }

    @Test
    public void withFieldsAndFiles()
        throws Exception {
        Part f0 = new VraptorPart("myField0", "the value");
        Part f1 = new VraptorPart("myField1", "other value");
        Part f2 = new VraptorPart("myFile0", "text/plain", "file1.txt", "vraptor3".getBytes());
        Part f3 = new VraptorPart("myFile1", "text/plain", "file2.txt", "vraptor3".getBytes());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParts()).thenReturn(Arrays.asList(f0, f1, f2, f3));

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(3)).setParameter(anyString(), anyString());
        
        verify(parameters).setParameter("myField0", "the value");
        verify(parameters).setParameter("myField1", "other value");
        
        verify(parameters).setParameter("myFile0", "myFile0");
        verify(parameters).setParameter("myFile1", "myFile1");
        
        verify(request).setAttribute(eq("myFile0"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myFile1"), any(UploadedFile.class));
    }

    @Test
    public void filesWithSameName()
        throws Exception {
        Part file0 = new VraptorPart("myfile0", "text/plain", "file.txt", "vraptor3".getBytes());
        Part file1 = new VraptorPart("myfile1", "text/plain", "file.txt", "vraptor3".getBytes());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParts()).thenReturn(Arrays.asList(file0, file1));

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("myfile0", "myfile0");
        verify(parameters).setParameter("myfile1", "myfile1");
        
        verify(request).setAttribute(eq("myfile0"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myfile1"), any(UploadedFile.class));
    }
    
    @Test
    public void multipleUpload()
        throws Exception {
        Part file0 = new VraptorPart("myfile0[]", "text/plain", "file.txt", "vraptor3".getBytes());
        Part file1 = new VraptorPart("myfile0[]", "text/plain", "file.txt", "vraptor3".getBytes());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(request.getParts()).thenReturn(Arrays.asList(file0, file1));

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("myfile0[0]", "myfile0[0]");
        verify(parameters).setParameter("myfile0[1]", "myfile0[1]");
        
        verify(request).setAttribute(eq("myfile0[0]"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myfile0[1]"), any(UploadedFile.class));
    }
}
