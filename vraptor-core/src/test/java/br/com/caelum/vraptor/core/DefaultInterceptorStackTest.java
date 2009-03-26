package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorStackTest {
    
    int count;
    
    @Before
    public void setup() {
        count = 0;
    }
    
    @Test
    public void testInvokesAllInterceptorsInItsCorrectOrder() throws IOException, InterceptionException {
        DefaultInterceptorStack stack = new DefaultInterceptorStack(null);
        CountInterceptor first = new CountInterceptor();
        CountInterceptor second = new CountInterceptor();
        stack.add(first);
        stack.add(second);
        stack.next(null,null);
        assertThat(first.run, is(equalTo(0)));
        assertThat(second.run, is(equalTo(1)));
    }
    
    class CountInterceptor implements Interceptor{
        int run;

        public void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException {
            run = count++;
            invocation.next(method, resourceInstance);
        }
    }

}
