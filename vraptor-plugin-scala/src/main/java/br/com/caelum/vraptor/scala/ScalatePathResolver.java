package br.com.caelum.vraptor.scala;

import java.io.File;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.http.FormatResolver;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alberto Souza
 * @author Bruno Oliveira
 * 
 */

@Component
@RequestScoped
public class ScalatePathResolver extends DefaultPathResolver {

	private ServletContext context;

	public ScalatePathResolver(ServletContext context, FormatResolver format) {
		super(format);
		this.context = context;
	}

	protected String getPrefix() {
		return "/WEB-INF/ssp/";
	}

	protected String getExtension() {
		return "ssp";
	}

	@Override
	public String pathFor(ResourceMethod method) {
		String path = super.pathFor(method);
		String realPathToViewFile = context.getRealPath(path);

		return new File(realPathToViewFile).exists() ? path : path.replace(
				"/WEB-INF/ssp", "/WEB-INF/jsp").replace(".ssp", ".jsp");
	}

}
