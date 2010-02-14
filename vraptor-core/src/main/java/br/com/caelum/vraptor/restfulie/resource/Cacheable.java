package br.com.caelum.vraptor.restfulie.resource;

/**
 * An interface that defines caching characteristics for a resource.<br/>
 * Annotations where avoided in order to provide programmatic support to
 * customization in a resource-by-resource instance basis, not at class level.
 * 
 * @author guilherme silveira
 */
public interface Cacheable {

	/**
	 * Returns the maximum number of seconds this resource can be cached for.
	 * 
	 * @return
	 */
	int getMaximumAge();

}
