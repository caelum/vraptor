package br.com.caelum.vraptor.scala

import scala.reflect.BeanProperty

/*
 * A simple example model.
 * The @BeanProperty annotation is only required if this class
 * is used at JSP view files.
 */
case class MyModel(@BeanProperty data:String)