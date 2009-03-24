package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;

public interface ResourceMethod {
	
	Method getMethod();

	Resource getResource();

}
