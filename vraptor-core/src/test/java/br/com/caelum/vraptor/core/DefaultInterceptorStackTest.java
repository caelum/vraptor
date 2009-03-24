package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Interceptor;

public class DefaultInterceptorStackTest {
    
    int count;
    
    @Before
    public void setup() {
        count = 0;
    }
    
    @Test
    public void testInvokesAllInterceptorsInItsCorrectOrder() {
        DefaultInterceptorStack stack = new DefaultInterceptorStack();
        CountInterceptor first = new CountInterceptor();
        CountInterceptor second = new CountInterceptor();
        stack.add(first);
        stack.add(second);
        stack.next();
        assertThat(first.run, is(equalTo(0)));
        assertThat(second.run, is(equalTo(1)));
    }
    
    class CountInterceptor implements Interceptor{
        int run;

        public void intercept(InterceptorStack invocation) {
            run = count++;
            invocation.next();
        }
    }

}
