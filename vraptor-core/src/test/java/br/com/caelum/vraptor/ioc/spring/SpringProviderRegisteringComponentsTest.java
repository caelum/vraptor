package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.Execution;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.jmock.Expectations;

import javax.servlet.http.HttpServletResponse;

public class SpringProviderRegisteringComponentsTest extends GenericContainerTest {
    private int counter;

    protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

    protected <T> T executeInsideRequest(final Execution<T> execution) {
        final Object[] holder = new Object[1];
        new Thread(new Runnable() {
            public void run() {
                HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
                HttpServletRequestMock httpRequest = new HttpServletRequestMock(session);
                HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
                VRaptorRequest request = new VRaptorRequest(context, httpRequest, response);
                VRaptorRequestHolder.setRequestForCurrentThread(request);
                T result = execution.execute(request, counter);
                VRaptorRequestHolder.resetRequestForCurrentThread();
                holder[0] = result;
                holder.notifyAll();
            }
        }).start();
        
        while (holder[0] == null) {
            try {
                holder.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (T) holder[0];
    }

    @Override
    protected void configureExpectations() {
        mockery.checking(new Expectations() {
            {
                allowing(context).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
                will(returnValue("no.packages"));
            }
        });
    }
}