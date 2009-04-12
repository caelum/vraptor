package br.com.caelum.vraptor.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

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

    @Test
    public void testFindLocaleProvidedByTheClient() {
        // there aren't locales in any context

        Locale expected = webRequest.getRequest().getLocale();
        assertNotNull(expected);

        // get the Locale sent by the client (on the request, browser
        // configuration)
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertNotNull(locale);
        assertEquals(expected, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleInRequest() {
        webRequest.getRequest().setAttribute(JSTL_LOCALE_KEY + ".request", Locale.GERMANY);
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.GERMANY, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleInSession() {
        JstlWrapper jstlWrapper = new JstlWrapper();

        webRequest.getRequest().getSession().setAttribute(JSTL_LOCALE_KEY + ".session", Locale.GERMANY);
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.GERMANY, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleInApplication() {
        JstlWrapper jstlWrapper = new JstlWrapper();

        webRequest.getServletContext().setAttribute(JSTL_LOCALE_KEY + ".application", Locale.GERMANY);
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.GERMANY, locale);
    }

    @Test
    public void testFindLocaleAsInitParameter() {
        JstlWrapper jstlWrapper = new JstlWrapper();
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(JSTL_LOCALE_KEY);
                will(returnValue("de_DE"));
            }
        });
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.GERMANY, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleShouldUseTheSmallestContext() {
        webRequest.getRequest().setAttribute(JSTL_LOCALE_KEY + ".request", Locale.GERMANY);
        webRequest.getRequest().getSession().setAttribute(JSTL_LOCALE_KEY + ".session", Locale.FRANCE);
        webRequest.getServletContext().setAttribute(JSTL_LOCALE_KEY + ".application", Locale.ITALY);
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(JSTL_LOCALE_KEY);
                will(returnValue("en_GB"));
            }
        });
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.GERMANY, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleShouldUseTheSmallestContextWhenAllButRequestAreFilled() {
        webRequest.getRequest().getSession().setAttribute(JSTL_LOCALE_KEY + ".session", Locale.FRANCE);
        webRequest.getServletContext().setAttribute(JSTL_LOCALE_KEY + ".application", Locale.ITALY);
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(JSTL_LOCALE_KEY);
                will(returnValue("en_GB"));
            }
        });
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.FRANCE, locale);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFindLocaleShouldUseTheSmallestContextWhenOnlyAppAndInitParameterAreSet() {

        webRequest.getServletContext().setAttribute(JSTL_LOCALE_KEY + ".application", Locale.ITALY);
        mockery.checking(new Expectations() {
            {
                one(context).getInitParameter(JSTL_LOCALE_KEY);
                will(returnValue("en_GB"));
            }
        });
        Locale locale = jstlWrapper.findLocale(webRequest);
        assertEquals(Locale.ITALY, locale);
        mockery.assertIsSatisfied();
    }

}
