package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;

public interface StereotypeHandler {
	public Class<? extends Annotation> stereotype();
	public void handle(Class<?> annotatedType);
}
