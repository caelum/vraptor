package br.com.caelum.vraptor.resource;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;

@Resource
public class VRaptorInfo {
    
    private final HttpServletResponse response;

    public VRaptorInfo(HttpServletResponse response) {
        this.response = response;
    }
    
    @Path("/is_using_vraptor")
    public void info() throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("vraptor3");
    }

}
