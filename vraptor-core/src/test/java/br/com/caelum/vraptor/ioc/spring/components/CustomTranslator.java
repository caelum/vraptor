package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Fabio Kung
 */
public class CustomTranslator implements UrlToResourceTranslator {
    public ResourceMethod translate(HttpServletRequest request) {
        return null;
    }
}
