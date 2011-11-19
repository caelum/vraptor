package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Test class for uploading features using commons-fileupload.
 * 
 * @author Otávio Scherer Garcia
 */
public class CommonsUploadMultipartInterceptorTest {

    private Object instance;
    @Mock private InterceptorStack stack;
    @Mock private ResourceMethod method;
    @Mock private HttpServletRequest request;
    @Mock private MutableRequest parameters;
    @Mock private Validator validator;
    private MultipartConfig config;
    private CommonsUploadMultipartInterceptor interceptor;
    private ServletFileUpload mockUpload;
    private ServletFileUploadCreator mockCreator;

    @Before
    public void setup()
        throws Exception {
        config = new DefaultMultipartConfig();

        MockitoAnnotations.initMocks(this);

        mockCreator = mock(ServletFileUploadCreator.class);
        mockUpload = mock(ServletFileUpload.class);
        when(mockCreator.create(Mockito.any(FileItemFactory.class))).thenReturn(mockUpload);
    }

    @Test
    public void shouldNotAcceptFormURLEncoded()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(false));
    }

    @Test
    public void shouldAcceptMultipart()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(true));
    }

    @Test
    public void withFieldsOnly()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("foo", "blah"));
        elements.add(new MockFileItem("bar", "blah blah"));

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("foo", "blah");
        verify(parameters).setParameter("bar", "blah blah");
    }

    @Test
    public void withFilesAndFields()
        throws Exception {

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("foo", "blah"));
        elements.add(new MockFileItem("bar", "blah blah"));
        elements.add(new MockFileItem("thefile0", "foo.txt", "foo".getBytes()));
        elements.add(new MockFileItem("thefile1", "bar.txt", "bar".getBytes()));

        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("foo", "blah");
        verify(parameters).setParameter("bar", "blah blah");
        
        verify(parameters).setParameter("thefile0", "thefile0");
        verify(parameters).setParameter("thefile1", "thefile1");
        
        verify(request).setAttribute(eq("thefile0"), any(UploadedFile.class));
        verify(request).setAttribute(eq("thefile1"), any(UploadedFile.class));
    }

    @Test
    public void fieldsWithSameName()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("myfile0", "foo.txt", "foo".getBytes()));
        elements.add(new MockFileItem("myfile1", "foo.txt", "bar".getBytes()));

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("myfile0", "myfile0");
        verify(parameters).setParameter("myfile1", "myfile1");
        
        verify(request).setAttribute(eq("myfile0"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myfile1"), any(UploadedFile.class));
    }
    
    @Test
    public void multipleUpload()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("myfile0[]", "foo.txt", "foo".getBytes()));
        elements.add(new MockFileItem("myfile0[]", "foo.txt", "bar".getBytes()));

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);
        
        System.out.println(parameters.getParameterMap());

        verify(parameters).setParameter("myfile0[0]", "myfile0[0]");
        verify(parameters).setParameter("myfile0[1]", "myfile0[1]");
        
        verify(request).setAttribute(eq("myfile0[0]"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myfile0[1]"), any(UploadedFile.class));
    }
}
