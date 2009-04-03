package br.com.caelum.vraptor.http;

import java.lang.reflect.Type;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface TypeCreator {

    Class<?> typeFor(ResourceMethod method);

    String nameFor(Type paramType);

}
