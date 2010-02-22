package br.com.caelum.vraptor.blank

import javax.servlet.ServletContextAttributeEvent
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextAttributeListener
import javax.servlet.ServletRequestAttributeEvent
import javax.servlet.ServletRequestAttributeListener
import javax.servlet.http.HttpSessionAttributeListener
import javax.servlet.http.HttpSessionBindingEvent
import scala.collection.JavaConversions._

class ScopesListener extends ServletContextAttributeListener with HttpSessionAttributeListener with ServletRequestAttributeListener {
	
	private def convertToJavaIterableIfIsScalaIterable(value:Any) = value match {			
			case m:scala.collection.mutable.Map[_,_] => asMap(m)
			case s:scala.collection.mutable.Set[_] => asSet(s)
			case b:scala.collection.mutable.Buffer[_] => asList(b)
			case l:Seq[_] => asCollection(l)
			case i:Iterable[_]  => asIterable(i)			
			case _ => value
			
		}

	def attributeAdded(event:ServletRequestAttributeEvent) {
		event.getServletRequest.setAttribute(event.getName(),convertToJavaIterableIfIsScalaIterable(event.getValue))
		
	}
	
	def attributeAdded(event:HttpSessionBindingEvent) {		
		event.getSession.setAttribute(event.getName(),convertToJavaIterableIfIsScalaIterable(event.getValue))
	}
	
	def attributeAdded(event:ServletContextAttributeEvent) {		
		event.getServletContext.setAttribute(event.getName(),convertToJavaIterableIfIsScalaIterable(event.getValue))
	}			
	
	def attributeRemoved(arg0:HttpSessionBindingEvent) {}

	def attributeReplaced(arg0:HttpSessionBindingEvent) {}
	
	def attributeRemoved(arg0:ServletRequestAttributeEvent) {}

	def attributeReplaced(arg0:ServletRequestAttributeEvent) {}

	def attributeRemoved(arg0:ServletContextAttributeEvent) {}

	def attributeReplaced(arg0:ServletContextAttributeEvent) {}
	
	
}
