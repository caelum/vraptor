package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class PicoProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvidePicoSpecificApplicationScopedComponents() {
        checkAvailabilityFor(true, DirScanner.class, ResourceLocator.class);
        mockery.assertIsSatisfied();
    }

    protected ContainerProvider getProvider() {
        return new PicoProvider();
    }

    protected <T> T executeInsideRequest(WhatToDo<T> execution) {
        HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        HttpServletRequestMock request = new HttpServletRequestMock(session);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        configureExpectations(request);
        VRaptorRequest webRequest = new VRaptorRequest(context, request, response);
        return execution.execute(webRequest, counter);
    }

    /**
     * Children providers can set custom expectations on request.
     */
    protected void configureExpectations(HttpServletRequestMock request) {
    }

    /**
     * Children providers can set custom expectations.
     */
    @Override
    protected void configureExpectations() {
       try {
            mockery.checking(new Expectations() {
                {
                    File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
                    File tmp = new File(tmpDir, "_tmp_vraptor_test");
                    tmp.mkdir();
                    allowing(context).getRealPath("");
                    will(returnValue(tmp.getAbsolutePath()));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
