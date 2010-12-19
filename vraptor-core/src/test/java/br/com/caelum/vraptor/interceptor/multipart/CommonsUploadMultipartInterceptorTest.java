package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
    @Mock
    private InterceptorStack stack;
    @Mock
    private ResourceMethod method;
    @Mock
    private HttpServletRequest request;
    @Mock
    private MutableRequest parameters;
    @Mock
    private Validator validator;
    private MultipartConfig config;
    private CommonsUploadMultipartInterceptor interceptor;

    @Before
    public void setup()
        throws Exception {
        config = new DefaultMultipartConfig();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void notAcceptsWithFormURLEncoded()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(false));
    }

    @Test
    public void shouldAcceptsWithMultipart()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, null);

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        assertThat(interceptor.accepts(method), equalTo(true));
    }

    @Test
    public void withOnlyFields()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, new OnlyFieldsCommonsFileUpload());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(2)).setParameter(anyString(), anyString());
    }

    @Test
    public void withFilesAndFields()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator, new FilesAndFieldsCommonsFileUpload());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(4)).setParameter(anyString(), anyString());
    }

    @Test
    public void fieldsWithSameName()
        throws Exception {
        interceptor = new CommonsUploadMultipartInterceptor(request, parameters, config, validator,
                new FilesWithSameNameCommonsFileUpload());

        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getMethod()).thenReturn("POST");

        interceptor.intercept(stack, method, instance);

        verify(parameters, atLeast(2)).setParameter(anyString(), anyString());
    }

    // -- inner classes

    public class OnlyFieldsCommonsFileUpload
        implements ServletFileUploadCreator {

        @Override
        public ServletFileUpload create(FileItemFactory fileItemFactory) {
            return new ServletFileUpload(fileItemFactory) {

                @Override
                public List<FileItem> parseRequest(HttpServletRequest request)
                    throws FileUploadException {
                    FileItem f0 = new MockFileItem("foo", "blah");
                    FileItem f1 = new MockFileItem("bar", "blah blah");

                    return Arrays.asList(f0, f1);
                }
            };
        }
    }

    public class FilesAndFieldsCommonsFileUpload
        implements ServletFileUploadCreator {

        @Override
        public ServletFileUpload create(FileItemFactory fileItemFactory) {
            return new ServletFileUpload(fileItemFactory) {

                @Override
                public List<FileItem> parseRequest(HttpServletRequest request)
                    throws FileUploadException {
                    FileItem f0 = new MockFileItem("foo", "blah");
                    FileItem f1 = new MockFileItem("bar", "blah blah");
                    FileItem f2 = new MockFileItem("thefile0", "foo.txt", "foo".getBytes());
                    FileItem f3 = new MockFileItem("thefile1", "bar.txt", "bar".getBytes());

                    return Arrays.asList(f0, f1, f2, f3);
                }
            };
        }
    }

    public class FilesWithSameNameCommonsFileUpload
        implements ServletFileUploadCreator {

        @Override
        public ServletFileUpload create(FileItemFactory fileItemFactory) {
            return new ServletFileUpload(fileItemFactory) {

                @Override
                public List<FileItem> parseRequest(HttpServletRequest request)
                    throws FileUploadException {
                    FileItem f0 = new MockFileItem("myfile0", "foo.txt", "foo".getBytes());
                    FileItem f1 = new MockFileItem("myfile1", "foo.txt", "bar".getBytes());

                    return Arrays.asList(f0, f1);
                }
            };
        }
    }

}
