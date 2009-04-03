package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 * 
 * @author Guilherme Silveira
 */
public class GenericContainerTest {

    private Container container;
    private Mockery mockery;
    private VRaptorRequest request;

    @Before
    public void setup() throws IOException {
        this.mockery = new Mockery();
        final File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
        final File tmp = new File(tmpDir, "_tmp_vraptor_test");
        tmp.mkdir();
        final ServletContext context = mockery.mock(ServletContext.class);
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("");
                will(returnValue(tmp.getAbsolutePath()));
            }
        });
        HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        this.request = new VRaptorRequest(context, request, response);
        PicoProvider provider = new PicoProvider();
        provider.start(context);
        this.container = provider.provide(this.request);
        
    }

    @Test
    public void canProvideAllComponents() {
        check(HttpServletRequest.class, HttpServletResponse.class,
                VRaptorRequest.class, DefaultInterceptorStack.class, DefaultRequestExecution.class,
                ResourceLookupInterceptor.class, InstantiateInterceptor.class, DefaultResult.class,
                ExecuteMethodInterceptor.class, OgnlParametersProvider.class, DefaultConverters.class);
        container.register(mockery.mock(ResourceMethod.class));
        check(PageResult.class);
        mockery.assertIsSatisfied();
    }

    private void check(Class<?>...  components) {
        for (Class<?> component : components) {
            MatcherAssert.assertThat(canProvide(component), Matchers.is(Matchers.equalTo(true)));
        }
    }

    private Boolean canProvide(Class<?> type) {
        return container.instanceFor(type) != null;
    }

}
