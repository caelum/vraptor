package br.com.caelum.vraptor.config;

/**
 * Allows you to configure extra settings related to your application
 * @author guilherme silveira
 * @author lucas cavalcanti
 * @since 3.0.3
 */
public interface Configuration {

	/**
	 * Returns the application path, including the http protocol, i.e.: http://localhost:8080/context_name.<br>
	 * One can implement this method to return a fixed http/ip prefix.
	 */
	public String getApplicationPath();

}
