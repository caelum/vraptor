package br.com.caelum.vraptor.scala;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.http.FormatResolver;


@Component
@RequestScoped
public class ScalatePathResolver extends DefaultPathResolver {
	
	public ScalatePathResolver(FormatResolver format) {
		super(format);
	}

	protected String getPrefix() {
		return "/WEB-INF/ssp/";
	}
	
    protected String getExtension() {
        return "ssp";
    }

}
