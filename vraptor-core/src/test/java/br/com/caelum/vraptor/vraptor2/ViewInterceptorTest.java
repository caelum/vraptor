package br.com.caelum.vraptor.vraptor2;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewInterceptorTest {

    private Mockery mockery;
    private RequestResult requestResult;
    private PageResult result;
    private ViewInterceptor interceptor;
    private ComponentInfoProvider info;
    private ResourceMethod method;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.requestResult = new RequestResult();
        this.requestResult.setValue("ok");
        this.result = mockery.mock(PageResult.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.method = mockery.mock(ResourceMethod.class);
        this.interceptor = new ViewInterceptor(result, requestResult, info);
    }

    @Test
    public void shouldForward() throws SecurityException, InterceptionException {
        mockery.checking(new Expectations() {
            {
                one(info).shouldShowView(method); will(returnValue(true));
                one(result).forward("ok");
            }
        });
        interceptor.intercept(null, this.method, null);
        mockery.assertIsSatisfied();
    }
    class MyThrowable extends RuntimeException{
        private static final long serialVersionUID = 1L;
        
    }


    @Test
    public void doesNothingInAViewlessMethodResource() throws SecurityException, InterceptionException {
        mockery.checking(new Expectations() {
            {
                one(info).shouldShowView(method); will(returnValue(false));
            }
        });
        interceptor.intercept(null, this.method, null);
        mockery.assertIsSatisfied();
    }

}
