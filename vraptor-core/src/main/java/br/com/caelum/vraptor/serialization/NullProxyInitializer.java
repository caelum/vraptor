package br.com.caelum.vraptor.serialization;

/**
 * ProxyInitializer that does nothing.
 *
 * @author Tomaz Lavieri
 * @since 3.1.2
 */
public class NullProxyInitializer implements ProxyInitializer {

	public Class<?> getActualClass(Object obj) {
		return obj.getClass();
	}

	public void initialize(Object obj) {}

	public boolean isProxy(Class<?> obj) {
		return false;
	}

}
