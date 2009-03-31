package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface TypeCreator {

    Class<?> typeFor(ResourceMethod method);

}
