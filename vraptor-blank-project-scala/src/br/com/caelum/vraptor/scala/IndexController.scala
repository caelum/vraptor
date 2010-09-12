package br.com.caelum.scala

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;

@Resource
class IndexController {

	@Get @Path(Array("/"))
	def index = "It works!"
	
	@Get @Path(Array("/list"))
	def list = List("It", "works", "with", "lists")
}
