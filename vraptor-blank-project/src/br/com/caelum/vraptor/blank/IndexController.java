package br.com.caelum.vraptor.blank;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;

@Resource
public class IndexController {

	@Path("/")
	public void index() {
	}

	@Path("/teste/{d}")
	public void teste(Double d) {
		System.out.println(d);
	}
}
