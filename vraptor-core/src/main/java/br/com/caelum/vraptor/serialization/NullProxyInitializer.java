package br.com.caelum.vraptor.serialization;

public class NullProxyInitializer implements ProxyInitializer {

	public Class<?> getActualClass(Object obj) {
		return obj.getClass();
	}

	public void initialize(Object obj) {}

	public boolean isProxy(Class<?> obj) {
		return false;
	}

}
