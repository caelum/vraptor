package br.com.caelum.vraptor.scala
import br.com.caelum.vraptor.{Path, Get, Post, Resource};

@Resource
class IndexController {

	@Get @Path(Array("/"))
	def index = new MyModel("It works!")
	
	@Post @Path(Array("/"))
	def index(myModel:MyModel) = myModel
		
	@Get @Path(Array("/jsp"))
	def jsp = new MyModel("It works with JSP too!")
	
	@Get @Path(Array("/list"))
	def list = List("It", "works", "with", "lists")
}
