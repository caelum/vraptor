package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;

public interface StereotypeHandler {
	public Annotation stereotype();
	public void handle(Class<?> type);
}
