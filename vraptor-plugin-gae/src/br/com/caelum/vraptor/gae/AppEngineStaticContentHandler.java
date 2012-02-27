package br.com.caelum.vraptor.gae;

import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.DefaultStaticContentHandler;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class AppEngineStaticContentHandler extends DefaultStaticContentHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AppEngineStaticContentHandler.class);

	public AppEngineStaticContentHandler(ServletContext context) {
		super(context);
	}

	@Override
	public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException {
		if (request.getRequestURI().startsWith(request.getContextPath() + "/_ah")) {
			logger.debug("Requesting appEngine config URI: {}. Bypassing VRaptor", request.getRequestURI());
			return true;
		}
		return super.requestingStaticFile(request);
	}
}
