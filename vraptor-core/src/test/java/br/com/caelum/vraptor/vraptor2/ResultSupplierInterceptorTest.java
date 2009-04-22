package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.vraptor2.outject.DefaultOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ResultSupplierInterceptorTest {

    private Mockery mockery;
    private ComponentInfoProvider info;
    private ComponentRegistry container;
    private ResultSupplierInterceptor interceptor;
    private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = mockery.mock(ComponentRegistry.class);
        this.info = mockery.mock(ComponentInfoProvider.class);
        this.stack = mockery.mock(InterceptorStack.class);
        this.interceptor = new ResultSupplierInterceptor(container, info);
    }

    @Test
    public void registersJsonExporterForAjaxRequest() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(true));
                one(container).register(JsonOutjecter.class);
                one(stack).next(null, null);
            }
        });
        interceptor.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void registersDefaultExporterForOtherRequests() throws InterceptionException, IOException {
        mockery.checking(new Expectations() {
            {
                one(info).isAjax();
                will(returnValue(false));
                one(container).register(DefaultOutjecter.class);
                one(stack).next(null, null);
            }
        });
        interceptor.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

}
