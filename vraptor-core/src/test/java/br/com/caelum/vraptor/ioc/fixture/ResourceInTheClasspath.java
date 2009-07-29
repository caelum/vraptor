package br.com.caelum.vraptor.ioc.fixture;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;

@Resource
public class ResourceInTheClasspath {
	@Get
	public void logicMethod() {}
}
