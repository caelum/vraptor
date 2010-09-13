package br.com.caelum.vraptor.scala

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import java.lang.reflect.Method;
import br.com.caelum.vraptor.http.route.Router

@ApplicationScoped
/** This is a Scala-compatible path parser that uses Path annotations in order to create the routing rules.
 */
class ScalaRoutes(router:Router) extends PathAnnotationRoutesParser(router) {

	override protected def isEligible(method:Method) =	super.isEligible(method) &&
															!method.getDeclaringClass().equals(classOf[AnyRef]) &&
															!method.getDeclaringClass().equals(classOf[Any]) &&
															!method.getName().contains("$");

}
