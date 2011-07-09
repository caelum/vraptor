package br.com.caelum.vraptor.blank;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class IndexController {

	private final Result result;

	public IndexController(Result result) {
		this.result = result;
	}

	public void index() {
		result.include("variable", "VRaptor!");
	}
}
