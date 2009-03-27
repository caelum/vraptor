package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface PathResolver {

    String pathFor(ResourceMethod method, String result);

}
