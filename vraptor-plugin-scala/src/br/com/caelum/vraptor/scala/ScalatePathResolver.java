package br.com.caelum.vraptor.scala;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import javax.servlet.http.HttpServletRequest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.AcceptHeaderToFormat;


@Component
@RequestScoped
public class ScalatePathResolver extends DefaultPathResolver {
	
	public ScalatePathResolver(HttpServletRequest request, AcceptHeaderToFormat acceptHeaderToFormat) {
		super(request, acceptHeaderToFormat);
	}

	protected String getPrefix() {
		return "/WEB-INF/ssp/";
	}
	
    protected String getExtension() {
        return "ssp";
    }

}
