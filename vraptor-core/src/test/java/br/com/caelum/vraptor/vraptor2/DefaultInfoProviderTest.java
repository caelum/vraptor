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

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInfoProviderTest {
    
    private InfoProvider info;
    
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.info = new DefaultInfoProvider();
    }


    @Test
    public void shouldThreatViewParameterAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue(""));
                one(request).getParameter("view"); will(returnValue("ajax"));
            }
        });
        assertThat(info.isAjax(request), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatViewURIAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("somethig.ajax.logic"));
            }
        });
        assertThat(info.isAjax(request), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }


    @Test
    public void shouldNormalURIAsNotAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("somethig.non-ajax.logic"));
                one(request).getParameter("view"); will(returnValue("xml"));
            }
        });
        assertThat(info.isAjax(request), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldThreatViewlessAsNonDisplayView() throws NoSuchMethodException {
        ResourceMethod method = mockery.methodFor(DefaultComponents.class, "nothing");
        assertThat(info.shouldShowView(null, method), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatNormalMethod() throws NoSuchMethodException {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("somethig.non-ajax.logic"));
                one(request).getParameter("view"); will(returnValue("xml"));
            }
        });
        ResourceMethod method = mockery.methodFor(DefaultComponents.class, "showIt");
        assertThat(info.shouldShowView(request, method), is(equalTo(true)));
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
