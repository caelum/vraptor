package br.com.caelum.vraptor.jersey;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class JerseyResourceTranslator implements UrlToResourceTranslator{

	private Jersey jersey;

	public ResourceMethod translate(RequestInfo info)
			throws ResourceNotFoundException, MethodNotAllowedException {
//		if(jersey.canHandle(info)) {
//			
//		}
		return null;
	}

}
