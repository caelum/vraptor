VRaptor 3 Flex-plugin
=========

This is a plugin to call a VRator Controller from a flash RemoteObject via AMF

Configuring in your flex project
========================
In fact this plugin is a blazeds factory, all you need is register it.

In your service-config.xml add the VRaptor factory:
`	
  <!-- VRaptor factory registration -->
  <factories>
    <factory id="vraptor"
     class="br.com.caelum.vraptor.flex.VRaptorServiceFactory" />
  </factories>
`

Then, to register a new destination, use the full qualified name of your controller and the 
VRaptor Factory, like this:

`
 <destination id="myVRaptorController">
    <properties>
      <factory>vraptor</factory>
      <source>br.com.caelum.vraptor.example.MyController</source>
    </properties>
  </destination>
</service>
`


Building in your machine
========================

To copy the lib dependecies run:

	ant libs

And then configure the projects in your eclipse using classpath-example and project-example files.
