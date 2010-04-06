package br.com.caelum.vraptor.serialization;

public class IgnoreProxyInitializer implements ProxyInitializer {

	@Override
	public Class<?> getActualClass(Object obj) {
		return obj.getClass();
	}

	@Override
	public void initialize(Object obj) {}

	@Override
	public boolean isProxy(Class<?> obj) {
		return false;
	}

}
