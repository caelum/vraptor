package br.com.caelum.vraptor.vraptor2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Test;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.test.HttpSessionMock;

public class ProviderTest extends GenericContainerTest {
    private int counter;

    @Test
    public void canProvideVRaptor2SpecificApplicationScopedComponents() {
        checkAvailabilityFor(true, Config.class);
    }

    @Test
    public void canProvideVRaptor2SpecificRequestScopedComponents() {
        checkAvailabilityFor(false, ExecuteAndViewInterceptor.class, HibernateValidatorPluginInterceptor.class,
                ValidatorInterceptor.class, ViewInterceptor.class, ComponentInfoProvider.class,
                OutjectionInterceptor.class, RequestResult.class, AjaxInterceptor.class, ValidationErrors.class);
    }

    protected ContainerProvider getProvider() {
        return new Provider();
    }

    protected <T> T executeInsideRequest(WhatToDo<T> execution) {
        final HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        final MutableRequest request = mockery.mock(MutableRequest.class, "request" + ++counter);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("what.ever.request.uri"));
                allowing(request).getSession(); will(returnValue(session));
                allowing(request).getParameterMap(); will(returnValue(new HashMap()));
                allowing(request).getParameter("view"); will(returnValue(null));
            }
        });
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        RequestInfo webRequest = new RequestInfo(context, request, response);
        return execution.execute(webRequest, counter);
    }

    @Override
    protected void configureExpectations() {
        try {
            mockery.checking(new Expectations() {
                {
                    allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                    will(returnValue("non-existing-vraptor.xml"));
                    allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                    will(returnValue("views.properties"));

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
