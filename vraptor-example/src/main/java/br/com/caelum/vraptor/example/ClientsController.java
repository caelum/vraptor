package br.com.caelum.vraptor.example;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class ClientsController {

	private final Result result;

	public ClientsController(Result result) {
		this.result = result;
	}
	
	@Path("/clientes")
	public void add() {
		
	}
}
