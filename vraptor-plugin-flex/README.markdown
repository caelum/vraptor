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


In fact this plugin is a factory to your AMF (de)serializer, all you need is register it.

BlazeDS
--------

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


GraniteDS
--------

In your service-config.xml add the VRaptor factory:
    <!-- VRaptor factory registration -->
    <factories>
      <factory id="vraptorFactory" class="br.com.caelum.vraptor.flex.graniteds.VraptorServiceFactory" />
    </factories>


Then, to register a new destination, use the full qualified name of your controller and the 
VRaptor Factory, like this:
    <services>
      <service
          id="granite-service"
          class="flex.messaging.services.RemotingService"
          messageTypes="flex.messaging.messages.RemotingMessage">
        <destination id="test">
          <channels>
            <channel ref="my-graniteamf"/>
          </channels>
          <properties>
            <factory>vraptorFactory</factory>
            <source>br.com.caelum.testeGranite.controller.HelloController</source>
          </properties>
        </destination>
      </service>
    </services>


Building in your machine
========================

To copy the lib dependecies run:

	ant libs

And then configure the projects in your eclipse using classpath-example and project-example files.

To generate the jar run:

    ant
