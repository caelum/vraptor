package br.com.caelum.vraptor.interceptor.multipart;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Validations;

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
    public void setup() {
        config = new DefaultMultipartConfig();

        MockitoAnnotations.initMocks(this);

        mockCreator = mock(ServletFileUploadCreator.class);
        mockUpload = mock(ServletFileUpload.class);
        when(mockCreator.create(Mockito.any(FileItemFactory.class))).thenReturn(mockUpload);
    }

    @Test
    public void shouldNotAcceptFormURLEncoded() {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(false));
    }

    @Test
    public void shouldAcceptMultipart() {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(true));
    }

    @Test
    public void withFieldsOnly() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("foo", "blah"));
        elements.add(new MockFileItem("bar", "blah blah"));

        when(request.getCharacterEncoding()).thenReturn("utf-8");
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("foo", "blah");
        verify(parameters).setParameter("bar", "blah blah");
    }

    @Test
    public void withFieldsOnlyWithInvalidCharset() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("foo", "blah"));
        elements.add(new MockFileItem("bar", "blah blah"));

        when(request.getCharacterEncoding()).thenReturn("www");
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);

        verify(parameters).setParameter("foo", "blah");
        verify(parameters).setParameter("bar", "blah blah");
    }
    
    @Test
    public void withFilesAndFields() throws Exception {
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
    public void emptyFiles() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);
    	
        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("thefile0", "", new byte[0]));
        
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);
        
        interceptor.intercept(stack, method, instance);
    }
    
	@Test(expected = InvalidParameterException.class)
    public void throwsInvalidParameterExceptionIfIOExceptionOccurs() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);
    	
        MockFileItem item = new MockFileItem("thefile0", "file.txt", new byte[0]);
        item = spy(item);
        
        doThrow(new IOException()).when(item).getInputStream();
        
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(newArrayList(item));
        
        interceptor.intercept(stack, method, instance);
    }
	
    @Test
    public void fieldsWithSameName() throws Exception {
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
    public void multipleUpload() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("myfile0[]", "foo.txt", "foo".getBytes()));
        elements.add(new MockFileItem("myfile0[]", "foo.txt", "bar".getBytes()));

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);
        
        verify(parameters).setParameter("myfile0[0]", "myfile0[0]");
        verify(parameters).setParameter("myfile0[1]", "myfile0[1]");
        
        verify(request).setAttribute(eq("myfile0[0]"), any(UploadedFile.class));
        verify(request).setAttribute(eq("myfile0[1]"), any(UploadedFile.class));
    }
    
    @Test
    public void doNothingWhenFileUploadExceptionOccurs() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);
        
        when(mockUpload.parseRequest(request)).thenThrow(new FileUploadException());
    	
        interceptor.intercept(stack, method, instance);
    }

    @Test
    public void shouldValidateWhenSizeLimitExceededExceptionOccurs() throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);
        
        when(mockUpload.parseRequest(request)).thenThrow(new FileUploadBase.SizeLimitExceededException("", 0L, 0L));
    	
        interceptor.intercept(stack, method, instance);
        
        verify(validator).checking(any(Validations.class));
    }
    
    @Test
    public void shouldCreateDirInsideAppIfTempDirAreNotAvailable() throws Exception {
    	DefaultMultipartConfig configSpy = (DefaultMultipartConfig) Mockito.spy(config);
    	Mockito.doThrow(new IOException()).when(configSpy).createTempFile();
    	
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, configSpy, validator, mockCreator);

        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("myfile", "foo.txt", "bar".getBytes()));

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);
        
        verify(configSpy).createDirInsideApplication();
    }

    @Test
    public void checkUploadedFile() throws Exception {
        final List<FileItem> elements = new ArrayList<FileItem>();
        elements.add(new MockFileItem("thefile0", "text/plain", "foo.txt", "foo".getBytes()));
        
        final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, mockCreator);

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");
        
        Mockito.doAnswer(new Answer<Object>() {
        	public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
        		attributes.put((String) args[0], args[1]);
        		return null;
        	}
		}).when(request).setAttribute(Mockito.anyString(), Mockito.anyString());
        
        when(mockUpload.parseRequest(request)).thenReturn(elements);

        interceptor.intercept(stack, method, instance);
        
        UploadedFile file = (UploadedFile) attributes.get("thefile0");

		assertEquals("foo.txt", file.getFileName());
		assertEquals("text/plain", file.getContentType());
		assertEquals("foo", new String(toByteArray(file.getFile())));
    }
}
