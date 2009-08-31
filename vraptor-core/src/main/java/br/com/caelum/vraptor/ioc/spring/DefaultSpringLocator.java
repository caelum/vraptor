package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default implementation for SpringLocator.
 * It tries to use spring default location to create the ApplicationContext
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class DefaultSpringLocator implements SpringLocator {

	public ApplicationContext getApplicationContext(ServletContext servletContext) {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		if (context != null) {
			return context;
		} else if (DefaultSpringLocator.class.getResource("/applicationContext.xml") != null) {
			return new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		return null;
	}

}
