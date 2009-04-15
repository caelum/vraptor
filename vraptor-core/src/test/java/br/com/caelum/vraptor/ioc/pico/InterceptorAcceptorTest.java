package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorAcceptorTest {

    private List<Class<? extends Interceptor>> interceptors;
    private InterceptorAcceptor acceptor;

    @Before
    public void setup() {
        this.interceptors = new ArrayList<Class<? extends Interceptor>>();
        this.acceptor = new InterceptorAcceptor(interceptors);
    }

    @Intercepts
    class IgnorableIntercepts{
    }
    @Test
    public void shouldIgnoreInterceptsWithAFailingType() {
        acceptor.analyze(IgnorableIntercepts.class);
        assertThat(interceptors,hasSize(0));
    }

    @Intercepts
    public static class MySequence implements InterceptorSequence{
        @SuppressWarnings("unchecked")
        public Class<? extends Interceptor>[] getSequence() {
            return new Class[]{InterceptorAnnotated.class, InterceptorNotAnnotated.class};
        }
    }
    @Test
    public void shouldAddAllInterceptorsFromASequenceInItsOrder() {
        acceptor.analyze(MySequence.class);
        assertThat(interceptors,hasSize(2));
        assertThat(interceptors, contains(InterceptorAnnotated.class, InterceptorNotAnnotated.class));
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
