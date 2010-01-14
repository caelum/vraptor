package br.com.caelum.vraptor.config;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Basic implementation of an application configuration.<br>
 * 
 * @author guilherme silveira
 * @author lucas cavalcanti
 * @since 3.0.3
 */
@Component
@RequestScoped
public class ApplicationConfiguration implements Configuration {

	final HttpServletRequest request;

	public ApplicationConfiguration(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Returns the application path, including the http protocol.<br>
	 * One can implement this method to return a fixed http/ip prefix.
	 */
	public String getApplicationPath() {
		return "http://" + request.getServerName() 
			+ (request.getServerPort() != 80? ":" + request.getServerPort() : "") 
			+ request.getContextPath();
	}

}
