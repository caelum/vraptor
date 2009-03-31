package br.com.caelum.vraptor.resource;

import java.util.List;

/**
 * Keeps a list of searchable resources.
 * 
 * @author Guilherme Silveira
 */
public interface ResourceRegistry {

    void register(List<Resource> resources);

    ResourceMethod gimmeThis(String name, String methodName);

    List<Resource> all();

}
