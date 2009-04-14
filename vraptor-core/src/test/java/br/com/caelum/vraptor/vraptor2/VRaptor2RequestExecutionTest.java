package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;

public class VRaptor2RequestExecutionTest {

    private Mockery mockery;
    private InterceptorStack stack;
    private VRaptor2RequestExecution execution;
    private InstantiateInterceptor instantiator;
    private Config config;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.stack = mockery.mock(InterceptorStack.class);
        this.instantiator = new InstantiateInterceptor(null);
        this.config = mockery.mock(Config.class);
        this.execution = new VRaptor2RequestExecution(stack, instantiator, config);
    }

    @Test
    public void shouldAddInterceptorsInOrder() throws InterceptionException, IOException {
        final Sequence sequence = mockery.sequence("executionSequence");
        mockery.checking(new Expectations() {
            {
                one(stack).add(ResourceLookupInterceptor.class); inSequence(sequence);
                one(stack).add(InterceptorListPriorToExecutionExtractor.class); inSequence(sequence);
                one(stack).add(instantiator); inSequence(sequence);
                one(stack).add(Validator.class); inSequence(sequence);
                one(stack).add(ExecuteAndViewInterceptor.class); inSequence(sequence);
                one(stack).add(OutjectionInterceptor.class); inSequence(sequence);
                one(stack).add(ViewInterceptor.class); inSequence(sequence);
                one(stack).next(null, null); inSequence(sequence);
            }
        });
        execution.execute();
        mockery.assertIsSatisfied();
    }

}
