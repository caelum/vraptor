package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.core.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.aop.config.AopConfigUtils;

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

    public <T> T instanceFor(Class<T> type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Request prepare(HttpServletRequest request, HttpServletResponse response) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
