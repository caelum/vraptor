package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorAcceptorTest {

    private List<Class<? extends Interceptor>> interceptors;
    private InterceptorAcceptor acceptor;

    @Before
    public void setup() {
        this.interceptors = new ArrayList<Class<? extends Interceptor>>();
        this.acceptor = new InterceptorAcceptor(interceptors);
    }
    
    @Test
    public void shouldAcceptInterceptorsAnnotatedWithInterceptorAnnotation() {
        acceptor.analyze(InterceptorAnnotated.class);
        assertThat(interceptors.contains(InterceptorAnnotated.class), is(equalTo(true)));
    }

    @Test
    public void ignoresNonAnnotatedInterceptors() {
        acceptor.analyze(InterceptorNotAnnotated.class);
        assertThat(interceptors.contains(InterceptorNotAnnotated.class), is(equalTo(false)));
    }
    
    @Intercepts
    class InterceptorAnnotated implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws IOException, InterceptionException {
        }
        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }
    
    class InterceptorNotAnnotated implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws IOException, InterceptionException {
        }
        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }
}
