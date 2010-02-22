package br.com.caelum.vraptor.blank

import javax.servlet.{ServletContextListener,ServletContextEvent,ServletContext}
import javax.servlet.jsp.{JspApplicationContext,JspFactory}


class ScalaELResolverListener extends ServletContextListener{

	def contextInitialized(evt:ServletContextEvent ) {
		val context = evt getServletContext 
		val jspContext = JspFactory.getDefaultFactory.getJspApplicationContext(context)
		jspContext.addELResolver(new ScalaCollectionELResolver);
	}

	def contextDestroyed(evt:ServletContextEvent) = {}
}
