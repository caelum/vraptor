package br.com.caelum.vraptor;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.internal.ExpectationBuilder;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class VRaptorMockery {

    private Mockery mockery = new Mockery();

    public void assertIsSatisfied() {
        mockery.assertIsSatisfied();
    }

    public void checking(ExpectationBuilder expectations) {
        mockery.checking(expectations);
    }

    public <T> T mock(Class<T> typeToMock) {
        return mockery.mock(typeToMock);
    }

    public ResourceMethod method(final Method declaredMethod) {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        checking(new Expectations() {
            {
                one(method).getMethod();
                will(returnValue(declaredMethod));
            }
        });
        return method;
    }

}
