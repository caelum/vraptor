package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;

@Resource
public class DummyResource {
	public DummyResource() {
	}
	
	@Get
	public void dummyLogicMethod() {
	}
}
