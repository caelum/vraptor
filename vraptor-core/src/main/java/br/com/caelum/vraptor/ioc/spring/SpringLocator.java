package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

/**
 * Component for locating a Spring ApplicationContext, to set as VRaptorApplicationContext parent
 * @author Lucas Cavalcanti
 */
public interface SpringLocator {
	ApplicationContext getApplicationContext(ServletContext context);
}
