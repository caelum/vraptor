package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface ParametersProvider {

    Object[] getParametersFor(ResourceMethod method);

}
