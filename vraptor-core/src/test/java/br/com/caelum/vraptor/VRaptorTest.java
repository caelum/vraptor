package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class VRaptorTest {

    private Mockery mockery;

    @Before
    public void setup() {
        this.mockery = new Mockery();
    }

    @Test(expected = ServletException.class)
    public void testComplainsIfNotInAServletEnviroment() throws IOException, ServletException {
        ServletRequest request = mockery.mock(ServletRequest.class);
        ServletResponse response = mockery.mock(ServletResponse.class);
        new VRaptor().doFilter(request, response, null);
    }

}
