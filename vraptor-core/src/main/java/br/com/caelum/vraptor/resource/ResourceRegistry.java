package br.com.caelum.vraptor.resource;

import java.util.List;

/**
 * Keeps a list of searchable resources.
 * 
 * @author Guilherme Silveira
 */
public interface ResourceRegistry {

    void register(List<Resource> results);

    ResourceMethod gimmeThis(String resourceName, String methodName);

    List<Resource> all();

}
