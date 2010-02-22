package br.com.caelum.vraptor.blank

import javax.el.{ELContext,ELException,ELResolver}
import javax.servlet.jsp.el.ScopedAttributeELResolver
import scala.collection.JavaConversions._

class ScalaCollectionELResolver extends ELResolver{

	def getCommonPropertyType(context:ELContext,base:Object) = null
	
	@throws(classOf[ELException])
	def getType(context:ELContext,base:Object, property:Object)  = null

	@throws(classOf[ELException])
	def setValue(context:ELContext,base:Object, property:Object,value:Object) = null

	@throws(classOf[ELException])
	def isReadOnly(context:ELContext,base:Object, property:Object) = true

	def getFeatureDescriptors(context:ELContext,base:Object) = null

	@throws(classOf[ELException])
	def getValue(context:ELContext,base:Object, property:Object) = {		
		def convertToJavaIterableIfIsScalaIterable(value:Object) = value match {			
			case m:scala.collection.mutable.Map[_,_] => asMap(m)
			case s:scala.collection.mutable.Set[_] => asSet(s)
			case b:scala.collection.mutable.Buffer[_] => asList(b)
			case l:Seq[_] => asCollection(l)
			case i:Iterable[_]  => asIterable(i)			
			case _ => value
			
		}				
		//deve ter um jeito de fazer de uma maneira mais elegante em scala :)
		if(base==null) {			
			val scopeResolver = new ScopedAttributeELResolver
			val value = scopeResolver.getValue(context,base,property)
			convertToJavaIterableIfIsScalaIterable(value)
		}
		else {
			convertToJavaIterableIfIsScalaIterable(base)
		}

		
	}
	
	
	
}
