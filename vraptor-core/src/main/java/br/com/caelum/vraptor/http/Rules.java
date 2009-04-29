package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.resource.HttpMethod;

public class Rules {
	
	private static final Logger logger = LoggerFactory.getLogger(Rules.class);
	
	private Method currentMethod;
	private Class definingType;
	
	public void as(String uri, HttpMethod method) {
		
	}
	
	public void as(String uri) {
		
	}
	
	public <T> T accept(Class<T> type) {
		Enhancer e = new Enhancer();
		e.setSuperclass(type);
		e.setCallback(new MethodInterceptor() {

			public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Class<? extends Object> baseType = instance.getClass();
				definingType = lookFor(baseType, baseType, method);
				currentMethod = method;
				return null;
			}

			private Class lookFor(Class<? extends Object> baseType, Class currentType, Method method) {
				if(currentType.equals(Object.class)) {
					throw new IllegalArgumentException("Invalid rule registration, method " + method.getName() + " was not found, although it was declared at " + baseType.getName());
				}
				return null;
			}
		});
		return (T) e.create();
	}

}
