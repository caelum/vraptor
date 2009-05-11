package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.jmock.Expectations;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletResponse;

public class SpringProviderRegisteringComponentsTest extends GenericContainerTest {
    private int counter;

    protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "unchecked"})
    protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
        final Object[] holder = new Object[1];
        final Object lock = new Object();
        holder[0] = lock;
        new Thread(new Runnable() {
            public void run() {
                T result = null;
                try {
                    HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
                    HttpServletRequestMock httpRequest = new HttpServletRequestMock(session);
                    HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);

                    RequestInfo request = new RequestInfo(context, httpRequest, response);
                    VRaptorRequestHolder.setRequestForCurrentThread(request);

                    RequestContextListener contextListener = new RequestContextListener();
                    contextListener.requestInitialized(new ServletRequestEvent(context, httpRequest));
                    result = execution.execute(request, counter);
                    contextListener.requestDestroyed(new ServletRequestEvent(context, httpRequest));

                    VRaptorRequestHolder.resetRequestForCurrentThread();
                } finally {
                    synchronized (holder) {
                        holder[0] = result;
                        holder.notifyAll();
                    }
                }
            }
        }).start();

        while (holder[0] == lock) {
            try {
                synchronized (holder) {
                    holder.wait(1000);
                }
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