package br.com.caelum.vraptor.converter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;

public class JstlWrapperTest {

    public static final String JSTL_LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";

    private Mockery mockery;

    private ServletContext context;

    private HttpServletRequest request;

    private VRaptorRequest webRequest;

    private JstlWrapper jstlWrapper;

    private HttpSession session;

    @Before
    public void setup() {
        this.jstlWrapper = new JstlWrapper();
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.session = mockery.mock(HttpSession.class);
        this.webRequest = new VRaptorRequest(context, request, null);
        mockery.checking(new Expectations() {
            {
                allowing(request).getSession();
                will(returnValue(session));
            }
        });
    }

    @Test
    public void testFindNonExistentAttribute() {
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.nonexistent.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.nonexistent.attribute.session");
                will(returnValue(null));
                exactly(1).of(context).getAttribute("my.nonexistent.attribute.application");
                will(returnValue(null));
                exactly(1).of(context).getInitParameter("my.nonexistent.attribute");
                will(returnValue(null));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.nonexistent.attribute");
        assertNull(value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInRequestContext() {
        String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(2).of(request).getAttribute("my.attribute.request");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInSessionContext() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(2).of(session).getAttribute("my.attribute.session");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeInApplicationContext() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        String attributeValue = "myValue";

        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.attribute.session");
                will(returnValue(null));
                exactly(2).of(context).getAttribute("my.attribute.application");
                will(returnValue("myValue"));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindExistentAttributeAsInitParameter() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        final String attributeValue = "myValue";
        mockery.checking(new Expectations() {
            {
                exactly(1).of(request).getAttribute("my.attribute.request");
                will(returnValue(null));
                exactly(1).of(session).getAttribute("my.attribute.session");
                will(returnValue(null));
                exactly(1).of(context).getAttribute("my.attribute.application");
                will(returnValue(null));
                exactly(1).of(context).getInitParameter("my.attribute");
                will(returnValue(attributeValue));
            }
        });
        Object value = jstlWrapper.find(webRequest, "my.attribute");
        assertSame(attributeValue, value);
        mockery.assertIsSatisfied();
    }

}
