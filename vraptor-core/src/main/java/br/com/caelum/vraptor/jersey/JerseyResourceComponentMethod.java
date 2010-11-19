package br.com.caelum.vraptor.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;

import com.sun.jersey.spi.uri.rules.UriRule;

import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class JerseyResourceComponentMethod implements ResourceMethod{

	private final Iterator<UriRule> rules;

	public JerseyResourceComponentMethod(Iterator<UriRule> rules) {
		this.rules = rules;
	}

	public boolean containsAnnotation(Class<? extends Annotation> annotation) {
		return false;
	}

	public Method getMethod() {
		return null;
	}

	public ResourceClass getResource() {
		return null;
	}

}
