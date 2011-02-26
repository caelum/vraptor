package br.com.caelum.vraptor.jersey;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * A delegator from filter config to the servlet context.<br/>
 * This is a work around so far to not break compatibility with previous VRaptor
 * releases.
 * 
 * @author guilherme silveira
 */
@Component
@ApplicationScoped
public class JerseyFilterConfig implements FilterConfig {

	private final ServletContext context;

	public JerseyFilterConfig(ServletContext context) {
		this.context = context;
	}

	public String getFilterName() {
		return "vraptor";
	}

	public String getInitParameter(String arg0) {
		return context.getInitParameter(arg0);
	}

	public Enumeration<String> getInitParameterNames() {
		return context.getInitParameterNames();
	}

	public ServletContext getServletContext() {
		return context;
	}

}
