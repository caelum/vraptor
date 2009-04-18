package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.junit.Before;

import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

public class AjaxInterceptorTest {
    
    private Mockery mockery;
    private ComponentInfoProvider info;
    private HttpServletResponse response;
    private Outjecter outjecter;
    private AjaxInterceptor interceptor;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.interceptor = new AjaxInterceptor(outjecter,  response, info);
    }
    
    @Test
    public void invokesNextIfNonAjax() {
        
    }
    
    @Test
    public void name() {
        
    }

}
