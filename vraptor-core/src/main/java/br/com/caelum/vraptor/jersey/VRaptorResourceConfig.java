package br.com.caelum.vraptor.jersey;

import java.util.Map;

import javax.servlet.ServletException;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.container.servlet.WebConfig;

/**
 * A vraptor adaptor to a Jersey Resource configuration.
 * 
 * @author guilherme silveira
 */
public class VRaptorResourceConfig extends WebComponent {

	protected ResourceConfig getDefaultResourceConfig(
			Map<String, Object> props, WebConfig wc) throws ServletException {
		ResourceConfig config = super.getDefaultResourceConfig(props, wc);
		config.getSingletons().add(new FakeMethodDispatchProvider());
		return config;
	}
}
