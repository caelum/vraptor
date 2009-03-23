package br.com.caelum.vraptor.resource;

import java.util.List;

/**
 * Keeps a list of searchable resources.
 */
public interface ResourceRegistry {

	void register(List<Resource> results);

	ResourceMethod gimmeThis(String resourceName, String methodName);

}
