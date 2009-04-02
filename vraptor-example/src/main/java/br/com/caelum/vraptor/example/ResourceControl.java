package br.com.caelum.vraptor.example;

import java.io.IOException;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.jsp.PageResult;

@Resource
public class ResourceControl {
    
    private final ResourceRegistry registry;
    private final PageResult result;

    public ResourceControl(ResourceRegistry registry, PageResult result) {
        this.registry = registry;
        this.result = result;
    }
    
    @Path("/resources/list")
    public void list() throws ServletException, IOException {
        result.include("resources", registry.all());
        result.forward("ok");
    }

}
