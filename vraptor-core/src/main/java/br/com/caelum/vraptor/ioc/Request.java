package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.resource.ResourceMethod;

public interface Request {

	void execute(ResourceMethod method);

}
