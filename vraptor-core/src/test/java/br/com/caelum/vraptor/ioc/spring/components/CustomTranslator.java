package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Fabio Kung
 */
public class CustomTranslator implements UrlToResourceTranslator {

	public ResourceMethod translate(MutableRequest request) {
		return null;
	}
}
