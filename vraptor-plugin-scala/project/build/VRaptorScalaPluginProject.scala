import sbt._

class VRaptorScalaPluginProject(info:ProjectInfo) extends DefaultProject(info) {
  val mavensnapshots= "snaps"     at "https://oss.sonatype.org/content/repositories/snapshots"

  val vraptor       = "br.com.caelum"           % "vraptor"               % "3.3.2-SNAPSHOT"  % "compile" intransitive()
  val mirror        = "net.vidageek"            % "mirror"                % "1.6.1"           % "compile"
  val guice         = "com.google.inject"       % "guice"                 % "3.0"             % "compile" intransitive()
  val servlet       = "org.mortbay.jetty"       % "servlet-api-2.5"       % "6.1.14"          % "compile"
  val scalatest     = "org.scalatest"           % "scalatest"             % "1.3"             % "test"
  val mockito       = "org.mockito"             % "mockito-core"          % "1.8.5"           % "test"

}