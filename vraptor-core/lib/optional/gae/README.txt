To run vraptor in Google App Engine without problems forward this steps : 

	1 - Remove objenesis-x.x.jar from classpath
	2 - Edit web.xml and add the iogi param provider in br.com.caelum.vraptor.packages param-name
	      Example : 
	      
	<context-param>
		<param-name>br.com.caelum.vraptor.packages</param-name>
		<param-value>other_packages,br.com.caelum.vraptor.http.iogi</param-value>
	</context-param>   