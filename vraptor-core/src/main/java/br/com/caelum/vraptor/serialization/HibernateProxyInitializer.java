package br.com.caelum.vraptor.serialization;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class HibernateProxyInitializer implements ProxyInitializer {

	private static Class<?> hibernateProxyClass = getHibernateProxyClass();

	public HibernateProxyInitializer() {}

	public static Class<?> getHibernateProxyClass() {
		if (hibernateProxyClass != null) {
			return hibernateProxyClass;
		}

		try {
			return Class.forName("org.hibernate.proxy.HibernateProxy");
		} catch (ClassNotFoundException e) {
			//TODO loggar debug avisando que não há no path
			return null;
		}
	}

	public static boolean hibernateJarExists() {
		return hibernateProxyClass != null;
	}

	public boolean isProxy(Class<?> clazz) {
		return hibernateJarExists() && hibernateProxyClass.isAssignableFrom(clazz);
	}

	public void initialize(Object obj) {
		if (obj != null && isProxy(obj.getClass())) {
			initializeImpl(obj);
		}
	}

	private void initializeImpl(Object obj) {
		Object lazyInitializer = new Mirror().on(obj).invoke().method("getHibernateLazyInitializer").withoutArgs();
		new Mirror().on(lazyInitializer).invoke().method("initialize").withoutArgs();
	}

	public Class<?> getActualClass(Object obj) {
		return isProxy(obj.getClass()) ? getDelegateClass(obj) : obj.getClass();
	}

	private Class<?> getDelegateClass(Object obj) {
		Object lazyInitializer = new Mirror().on(obj).invoke().method("getHibernateLazyInitializer").withoutArgs();
		return (Class<?>) new Mirror().on(lazyInitializer).invoke().method("getPersistentClass").withoutArgs();
	}
}
