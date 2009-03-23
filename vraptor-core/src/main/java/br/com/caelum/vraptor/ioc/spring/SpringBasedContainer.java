package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.core.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.BeanFactoryUtils;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer implements Container {
    private GenericWebApplicationContext applicationContext;

    public SpringBasedContainer() {
        applicationContext = new GenericWebApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(applicationContext);
        
    }

    public void start() {
        new ComponentScanner(applicationContext).scan("br.com.caelum.vraptor");
        applicationContext.refresh();
        applicationContext.start();
    }

    public void stop() {
        applicationContext.stop();
        applicationContext.destroy();
    }

    public Request prepare(HttpServletRequest request, HttpServletResponse response) {
        return instanceFor(Request.class);
    }

    public <T> T instanceFor(Class<T> type) {
        return (T) BeanFactoryUtils.beanOfType(applicationContext, type);
    }
}
