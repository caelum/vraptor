package br.com.caelum.vraptor.scala

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor.http.route.TypeFinder;
import br.com.caelum.vraptor.ioc.Component;
import java.lang.reflect.Method;

@ApplicationScoped
@Component
/** This is a Scala-compatible path parser that uses Path annotations in order to create the routing rules.
 */
class ScalaRoutes(proxifier:Proxifier, finder:TypeFinder) extends PathAnnotationRoutesParser(proxifier, finder) {

	override protected def isEligible(method:Method) =	super.isEligible(method) &&
															!method.getDeclaringClass().equals(classOf[AnyRef]) &&
															!method.getDeclaringClass().equals(classOf[Any]) &&
															!method.getName().contains("$");

}
