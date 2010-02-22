package br.com.caelum.vraptor.blank

import br.com.caelum.vraptor.Result
import br.com.caelum.vraptor.Resource
import scala.collection.JavaConversions._




@Resource
class ProdutoController(result:Result) {
	
	def lista = List(new Produto("teste",20),new Produto("teste2",25.50))
	
	
	def adiciona(produto:Produto) =  {		
		result forwardTo classOf[ProdutoController] lista
	}
	
	
}

