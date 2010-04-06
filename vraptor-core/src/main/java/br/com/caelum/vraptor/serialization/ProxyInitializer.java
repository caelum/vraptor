package br.com.caelum.vraptor.serialization;

/**
 * Initializer of proxfied objets.
 * 
 * @author Tomaz Lavieri
 * @since 3.1.2
 */
public interface ProxyInitializer {
	
	/**
	 * Check if the <tt>clazz</tt> send is isAssignableFrom proxy.
	 */
	boolean isProxy(Class<?> clazz);
	
	/**
	 * Initialize the <tt>obj</tt> send if it is a proxy.
	 */
	void initialize(Object obj);
	
	/**
	 * Find the real class of the <tt>obj</tt> send. 
	 * @return the class of delegate object if the <tt>obj</tt> is a proxy or <tt>obj.getClass()</tt> otherwise.
	 */
	Class<?> getActualClass(Object obj);

}
