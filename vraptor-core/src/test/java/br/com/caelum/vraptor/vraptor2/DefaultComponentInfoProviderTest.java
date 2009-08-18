package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.Viewless;

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultComponentInfoProviderTest {
    
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }


    @Test
    public void shouldThreatViewParameterAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue(""));
                allowing(request).getParameter("view"); will(returnValue("ajax"));
            }
        });
        DefaultComponentInfoProvider info = new DefaultComponentInfoProvider(request);
        assertThat(info.isAjax(), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatViewURIAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("somethig.ajax.logic"));
            }
        });
        DefaultComponentInfoProvider info = new DefaultComponentInfoProvider(request);
        assertThat(info.isAjax(), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }


    @Test
    public void shouldNormalURIAsNotAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("somethig.non-ajax.logic"));
                allowing(request).getParameter("view"); will(returnValue("xml"));
            }
        });
        DefaultComponentInfoProvider info = new DefaultComponentInfoProvider(request);
        assertThat(info.isAjax(), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldThreatViewlessAsNonDisplayView() throws NoSuchMethodException {
        ResourceMethod method = mockery.methodFor(DefaultComponents.class, "nothing");
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("somethig.whatever.logic"));
                allowing(request).getParameter("view"); will(returnValue(null));
            }
        });
        DefaultComponentInfoProvider info = new DefaultComponentInfoProvider(request);
        assertThat(info.shouldShowView(method), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatNormalMethod() throws NoSuchMethodException {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(request).getRequestURI(); will(returnValue("somethig.non-ajax.logic"));
                allowing(request).getParameter("view"); will(returnValue("xml"));
            }
        });
        ResourceMethod method = mockery.methodFor(DefaultComponents.class, "showIt");
        DefaultComponentInfoProvider info = new DefaultComponentInfoProvider(request);
        assertThat(info.shouldShowView(method), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }


    @Component
    class DefaultComponents {
        @Viewless
        public void nothing() {
        }
        public void showIt() {
        }
    }

}
