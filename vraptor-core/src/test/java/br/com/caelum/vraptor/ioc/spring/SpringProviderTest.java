package br.com.caelum.vraptor.ioc.spring;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;

/**
 * @author Fabio Kung
 */
public class SpringProviderTest {
    private Mockery mockery;

    @Before
    public void init() {
        mockery = new Mockery();
    }

    public void destroy() {
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldLoadInitParameterForBasePackages() {
        final ServletContext servletContext = mockery.mock(ServletContext.class);
        mockery.checking(new Expectations() {{
            one(servletContext).getInitParameter("br.com.caelum.vraptor.spring.packages");
        }});
        SpringProvider provider = new SpringProvider();
        provider.start(servletContext);
    }
}
