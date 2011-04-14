import sbt._

class VRaptorProject(info:ProjectInfo) extends DefaultWebProject(info) {
  val mavensnapshots= "snaps"     at "https://oss.sonatype.org/content/repositories/snapshots"

  val vraptor       = "br.com.caelum"           % "vraptor"               % "3.3.2-SNAPSHOT"  % "compile"
  val scalate       = "org.fusesource.scalate"  % "scalate-core"          % "1.4.1"           % "compile"
  val servlet       = "org.mortbay.jetty"       % "servlet-api-2.5"       % "6.1.14"          % "compile"
  val jetty6        = "org.mortbay.jetty"       % "jetty"                 % "6.1.22"          % "test"
  val jsp           = "org.mortbay.jetty"       % "jsp-2.1"               % "6.1.14"          % "test"

  override def mainCompilePath = webappPath / "WEB-INF" / "classes"
  override def scanDirectories = mainCompilePath :: testCompilePath :: Nil

  override def jettyWebappPath  = webappPath

}
