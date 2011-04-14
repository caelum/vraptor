VRaptor 3 Flex-plugin
=========

This is a plugin to call a VRaptor Controller from a flash RemoteObject via AMF

Configuring in your flex project
========================
First of all configure the VRaptor classpath package scan adding the vraptor-pluging-flex
to it in your web.xml as follows:
	<context-param>
        	<param-name>br.com.caelum.vraptor.packages</param-name>
	        <param-value>br.com.caelum.vraptor.flex</param-value>
    </context-param>


In fact this plugin is a blazeds factory, all you need is register it.

In your service-config.xml add the VRaptor factory:
    <!-- VRaptor factory registration -->
    <factories>
      <factory id="vraptor" class="br.com.caelum.vraptor.flex.blazeds.VRaptorServiceFactory" />
    </factories>


Then, to register a new destination, use the full qualified name of your controller and the 
VRaptor Factory, like this:
    <destination id="myVRaptorController">
      <properties>
        <factory>vraptor</factory>
        <source>br.com.caelum.vraptor.example.MyController</source>
      </properties>
    </destination>



Building in your machine
========================

To copy the lib dependecies run:

	ant libs

And then configure the projects in your eclipse using classpath-example and project-example files.
