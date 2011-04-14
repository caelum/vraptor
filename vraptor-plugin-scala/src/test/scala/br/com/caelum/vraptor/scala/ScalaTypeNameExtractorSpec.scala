package br.com.caelum.vraptor.scala

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.lang.reflect.ParameterizedType

/**
 * Created by IntelliJ IDEA.
 * User: lucascs
 * Date: 4/14/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */

class ScalaTypeNameExtractorSpec extends FlatSpec with ShouldMatchers {

  "ScalaTypeNameExtractor" should "return List types" in {
    val extractor = new ScalaTypeNameExtractor

    extractor.nameFor(new ParameterizedType {
      def getOwnerType = null
      def getRawType = classOf[List[String]]
      def getActualTypeArguments = Array(classOf[String])
    }) should be === "stringList"

    extractor.nameFor(new ParameterizedType {
      def getOwnerType = null
      def getRawType = classOf[Array[Long]]
      def getActualTypeArguments = Array(classOf[Long])
    }) should be === "longList"

    extractor.nameFor(new ParameterizedType {
      def getOwnerType = null
      def getRawType = classOf[java.util.List[Int]]
      def getActualTypeArguments = Array(classOf[Int])
    }) should be === "intList"
  }
}