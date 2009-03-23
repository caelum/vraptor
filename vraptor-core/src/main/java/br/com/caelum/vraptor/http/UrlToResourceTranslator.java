package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface UrlToResourceTranslator {

	ResourceMethod translate(HttpServletRequest request);

}
