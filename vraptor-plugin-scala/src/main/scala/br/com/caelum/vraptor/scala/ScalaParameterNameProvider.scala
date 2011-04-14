package br.com.caelum.gnarus.infra.vraptor

import br.com.caelum.vraptor.http.ParameterNameProvider
import java.lang.reflect.AccessibleObject
import br.com.caelum.vraptor.ioc.{ApplicationScoped, Component}
import scala.collection.JavaConversions._

@Component
@ApplicationScoped
class ScalaParameterNameProvider(delegate:ParameterNameProvider) extends ParameterNameProvider {
  override def parameterNamesFor(accessible:AccessibleObject):Array[String] = {
    delegate.parameterNamesFor(accessible).map(_.replaceAll("\\$.*$", ""))
  }
}