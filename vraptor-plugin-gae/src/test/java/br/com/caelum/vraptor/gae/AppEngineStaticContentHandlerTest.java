package br.com.caelum.vraptor.gae;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AppEngineStaticContentHandlerTest {
    
    private AppEngineStaticContentHandler handler;
    
    private @Mock ServletContext context;
    private @Mock HttpServletRequest request;
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new AppEngineStaticContentHandler(context);
    }
    
    @Test
    public void shouldIgnoreAdminPages() throws Exception {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/_ah/jobs");
        
        assertThat(handler.requestingStaticFile(request), is(true));
    }
    
    @Test
    public void shouldExecuteNonAdminPages() throws Exception {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/my/uri");
        
        assertThat(handler.requestingStaticFile(request), is(false));
    }
}
