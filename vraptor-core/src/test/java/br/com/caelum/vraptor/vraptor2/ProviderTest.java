package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.Execution;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.validator.ValidationErrors;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;

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
                OutjectionInterceptor.class, RequestResult.class, ResultSupplierInterceptor.class,
                AjaxInterceptor.class, ValidationErrors.class);
    }

    @Before
    @Override
    public void setup() throws IOException {
        super.setup();
        mockery.checking(new Expectations() {
            {
                // TODO nasty, should be one()?
                allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue("non-existing-vraptor.xml"));
                allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue("views.properties"));
            }
        });
    }

    protected ContainerProvider getProvider() {
        return new Provider();
    }

    protected <T> T executeInsideRequest(Execution<T> execution) {
        final HttpSessionMock session = new HttpSessionMock(context, "session" + ++counter);
        HttpServletRequestMock httpRequest = new HttpServletRequestMock(session);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "response" + counter);
        VRaptorRequest request = new VRaptorRequest(context, httpRequest, response);
        return execution.execute(request, counter);
    }

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
