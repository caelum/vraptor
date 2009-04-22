package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fabio Kung
 */
public class SpringProviderTest {
    private Mockery mockery;
    private ServletContext servletContext;

    @Before
    public void init() {
        mockery = new Mockery();
        servletContext = mockery.mock(ServletContext.class);
        HttpServletRequestMock request = new HttpServletRequestMock(new HttpSessionMock(servletContext, "session"));
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @After
    public void destroy() {
        mockery.assertIsSatisfied();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldLoadInitParameterForBasePackages() {
        mockery.checking(new Expectations() {{
            one(servletContext).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
        }});
        SpringProvider provider = new SpringProvider();
        provider.start(servletContext);
    }
}