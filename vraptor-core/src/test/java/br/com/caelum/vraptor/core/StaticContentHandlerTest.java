package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class StaticContentHandlerTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private ServletContext context;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.context = mockery.mock(ServletContext.class);
    }

    @Test
    public void returnsTrueForRealStaticResources() throws Exception {
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); 
                File file = File.createTempFile("_test", ".xml");
                String key = file.getAbsolutePath();
                will(returnValue("/contextName/" +key));
                one(request).getContextPath(); will(returnValue("/contextName/"));
                one(context).getResource(key); will(returnValue(file.toURL()));
            }
        });
        boolean result = new StaticContentHandler(context).requestingStaticFile(request);
        assertThat(Boolean.valueOf(result), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsFalseForNonStaticResources() throws Exception {
        mockery.checking(new Expectations() {
            {
                File file = new File("_test_unknown.xml");
                String key = file.getAbsolutePath();
                one(request).getRequestURI(); 
                will(returnValue("/contextName/" +key));
                one(request).getContextPath(); will(returnValue("/contextName/"));
                one(context).getResource(key); will(returnValue(null));
            }
        });
        boolean result = new StaticContentHandler(context).requestingStaticFile(request);
        assertThat(Boolean.valueOf(result), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

}
