package br.com.caelum.vraptor.core;

import static javax.servlet.jsp.jstl.core.Config.FMT_FALLBACK_LOCALE;
import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALE;
import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALIZATION_CONTEXT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Test class for {@link JstlLocalization}.
 * 
 * @author Otávio Scherer Garcia
 */
public class JstlLocalizationTest {

    static final Locale PT_BR = new Locale("pt", "BR");

    private JstlLocalization localization;

    @Mock
    MutableRequest request;
    @Mock
    ServletContext servletContext;
    @Mock
    HttpSession session;

    private static ResourceBundle bundle = new ListResourceBundle() {
        protected Object[][] getContents() {
            return new Object[][] { { "my.key", "abc" } };
        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RequestInfo webRequest = new RequestInfo(servletContext, null, request, null);
        localization = new JstlLocalization(webRequest);

        LocalizationContext context = new LocalizationContext(bundle);
        when(request.getAttribute(FMT_LOCALIZATION_CONTEXT + ".request")).thenReturn(context);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void keyNotFound() {
        String value = localization.getMessage("my.notfound.key");
        assertThat(value, startsWith("???"));
        assertThat(value, endsWith("???"));
    }

    @Test
    public void keyFound() {
        String value = localization.getMessage("my.key");
        assertThat(value, equalTo("abc"));

        System.out.println(localization.getBundle().getKeys());
    }

    @Test
    public void findLocaleFromRequest() {
        when(request.getAttribute(FMT_LOCALE + ".request")).thenReturn(PT_BR);
        assertThat(localization.getLocale(), equalTo(PT_BR));
    }

    @Test
    public void findLocaleFromSession() {
        when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(PT_BR);
        assertThat(localization.getLocale(), equalTo(PT_BR));
    }

    @Test
    public void findLocaleFromServletContext() {
        when(servletContext.getInitParameter(FMT_LOCALE)).thenReturn(PT_BR.toString());
        assertThat(localization.getLocale(), equalTo(PT_BR));
    }

    @Test
    public void findLocaleFromRequestLocale() {
        when(request.getLocale()).thenReturn(PT_BR);
        assertThat(localization.getLocale(), equalTo(PT_BR));
    }

    @Test
    public void testLocaleWithSessionNotRequest() {
        when(request.getAttribute(FMT_LOCALE + ".request")).thenReturn(PT_BR);
        when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(Locale.ENGLISH);
        assertThat(localization.getLocale(), equalTo(PT_BR));
    }

    @Test
    public void testFallbackLocale() {
        when(request.getAttribute(FMT_FALLBACK_LOCALE + ".request")).thenReturn(PT_BR);
        assertThat(localization.getFallbackLocale(), equalTo(PT_BR));
    }
}
