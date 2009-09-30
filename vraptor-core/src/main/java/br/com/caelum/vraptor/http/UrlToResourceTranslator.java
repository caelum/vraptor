
package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Translates requests into resource methods.<br>
 * The basic idea is to analyze the uri to strip it of unwanted data and use the
 * rest to route for an specific resource.
 * 
 * @author Guilherme Silveira
 */
public interface UrlToResourceTranslator {

	ResourceMethod translate(MutableRequest request);

}
